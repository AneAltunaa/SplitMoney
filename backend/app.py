from flask import Flask, request, jsonify
import sqlite3
import firebase_admin
from firebase_admin import credentials, messaging
import os
import json

# --- Initialization ---
try:
    firebase_creds_json = os.environ.get('FIREBASE_CREDENTIALS')

    if firebase_creds_json:
        cred_dict = json.loads(firebase_creds_json)
        cred = credentials.Certificate(cred_dict)
        firebase_admin.initialize_app(cred)
        print("Firebase initialized successfully from Environment Variable")
    else:
        cred = credentials.Certificate("serviceAccountKey.json")
        firebase_admin.initialize_app(cred)
        print("Firebase initialized successfully from File")

except Exception as e:
    print(f"Warning: Firebase initialization failed. Notifications won't work. {e}")


app = Flask(__name__)
DB = "database.db"

def query(sql, args=(), one=False):
    conn = sqlite3.connect(DB)
    conn.row_factory = sqlite3.Row
    cur = conn.execute(sql, args)
    rows = cur.fetchall()
    conn.commit()
    conn.close()
    return (dict(rows[0]) if rows else None) if one else [dict(r) for r in rows]

# --- Send Notification ---
def send_push_notification(user_id, title, body):
    try:
        # ユーザーのトークンを取得
        user = query("SELECT fcm_token FROM users WHERE id=?", (user_id,), one=True)
        if user and user.get('fcm_token'):
            message = messaging.Message(
                notification=messaging.Notification(
                    title=title,
                    body=body,
                ),
                token=user['fcm_token'],
            )
            response = messaging.send(message)
            print(f"Notification sent to user {user_id}: {response}")
        else:
            print(f"No token found for user {user_id}")
    except Exception as e:
        print(f"Failed to send notification: {e}")


# ---------- USERS ----------
@app.post("/users/register")
def register_user():
    data = request.json
    query("INSERT INTO users (name, lastname, mail, phone, password) VALUES (?, ?, ?, ?, ?)",
          (data["name"], data["lastname"], data["mail"], data["phone"], data["password"]))
    return jsonify({"message": "User registered"})

@app.post("/users/login")
def login_user():
    d = request.json
    u = query("SELECT * FROM users WHERE mail=? AND password=?", (d["mail"], d["password"]), one=True)
    return jsonify(u or {"error": "Invalid credentials"})

@app.get("/users")
def get_all_users(): return jsonify(query("SELECT * FROM users"))

@app.get("/users/<int:id>")
def get_user_by_id(id): return jsonify(query("SELECT * FROM users WHERE id=?", (id,), one=True))

@app.get("/users/mail/<mail>")
def get_user_by_mail(mail): return jsonify(query("SELECT * FROM users WHERE mail=?", (mail,), one=True))

@app.put("/users/<int:id>")
def update_user(id):
    d = request.json
    query("""UPDATE users SET name=?, lastname=?, mail=?, phone=?, password=? WHERE id=?""",
          (d["name"], d["lastname"], d["mail"], d["phone"], d["password"], id))
    return jsonify({"message": "User updated"})

@app.delete("/users/<int:id>")
def delete_user(id):
    query("DELETE FROM users WHERE id=?", (id,))
    return jsonify({"message": "User deleted"})

@app.put("/users/<int:id>/token")
def update_user_token(id):
    d = request.json
    token = d.get("token")
    query("UPDATE users SET fcm_token=? WHERE id=?", (token, id))
    return jsonify({"message": "Token updated"})

# ---------- GROUPS ----------
@app.post("/groups")
def create_group():
    d = request.json
    conn = sqlite3.connect(DB)
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()

    cur.execute("INSERT INTO groups (name, description) VALUES (?, ?)", (d["name"], d["description"]))
    gid = cur.lastrowid

    for uid in d.get("participants", []):
        cur.execute("INSERT OR IGNORE INTO group_users (group_id, user_id) VALUES (?, ?)", (gid, uid))

    participants = d.get("participants", [])
    creator_id = d.get("created_by")

    for uid in d.get("participants", []):
        cur.execute("INSERT OR IGNORE INTO group_users (group_id, user_id) VALUES (?, ?)", (gid, uid))

        should_notify = True
        if creator_id and uid == creator_id:
            should_notify = False

        if should_notify:
            send_push_notification(uid, "New Group Invitation", f"You have been added to group '{d['name']}'")


    conn.commit()
    conn.close()
    return jsonify({"message": "Group created", "group_id": gid})

@app.get("/groups/<int:id>")
def get_group_by_id(id): return jsonify(query("SELECT * FROM groups WHERE id=?", (id,), one=True))

@app.get("/groups/user/<int:uid>")
def get_groups_by_user(uid):
    return jsonify(query("""SELECT g.* FROM groups g 
                            JOIN group_users gu ON g.id=gu.group_id 
                            WHERE gu.user_id=?""", (uid,)))

@app.put("/groups/<int:id>")
def update_group(id):
    d = request.json
    query("UPDATE groups SET name=?, description=? WHERE id=?", (d["name"], d["description"], id))
    return jsonify({"message": "Group updated"})

@app.delete("/groups/<int:id>")
def delete_group(id):
    query("DELETE FROM groups WHERE id=?", (id,))
    return jsonify({"message": "Group deleted"})

# ---------- GROUP USERS ----------
@app.post("/group_users")
def add_participant():
    d = request.json
    print("DATA RECEIVED:", d)
    query("INSERT OR IGNORE INTO group_users (group_id, user_id) VALUES (?, ?)", (d["group_id"], d["user_id"]))
    return jsonify({"message": "Participant added"})

@app.get("/group_users/<int:gid>")
def get_participants(gid):
    return jsonify(query("""SELECT u.* FROM users u 
                            JOIN group_users gu ON u.id=gu.user_id 
                            WHERE gu.group_id=?""", (gid,)))

@app.delete("/group_users")
def delete_participant():
    d = request.json
    query("DELETE FROM group_users WHERE group_id=? AND user_id=?", (d["group_id"], d["user_id"]))
    return jsonify({"message": "Participant removed"})

# ---------- EXPENSES ----------
@app.post("/expenses")
def add_expense():
    d = request.json
    conn = sqlite3.connect(DB)
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()


    cur.execute(
        "INSERT INTO expenses (group_id, description, total_amount, paid_by) VALUES (?, ?, ?, ?)",
        (d["group_id"], d["description"], d["total_amount"], d["paid_by"])
    )
    expense_id = cur.lastrowid

    shares = d.get("shares")

    if shares:

        for s in shares:
            cur.execute("""
                INSERT INTO expense_shares (expense_id, user_id, amount_owed, paid)
                VALUES (?, ?, ?, 0)
            """, (expense_id, s["user_id"], s["amount_owed"]))
    else:

        cur.execute("SELECT user_id FROM group_users WHERE group_id = ?", (d["group_id"],))
        members = [r["user_id"] for r in cur.fetchall()]
        if members:
            share_amount = float(d["total_amount"]) / len(members)
            for uid in members:
                cur.execute("""
                    INSERT INTO expense_shares (expense_id, user_id, amount_owed, paid)
                    VALUES (?, ?, ?, 0)
                """, (expense_id, uid, share_amount))

    conn.commit()
    conn.close()

    try:
        payer = query("SELECT name FROM users WHERE id=?", (d["paid_by"],), one=True)
        payer_name = payer['name'] if payer else "Someone"

        members = query("SELECT user_id FROM group_users WHERE group_id=?", (d["group_id"],))

        for m in members:
            if m['user_id'] != d['paid_by']:
                send_push_notification(
                    m['user_id'],
                    "New Expense",
                    f"{payer_name} added '{d['description']}' ($ {d['total_amount']})"
                )
    except Exception as e:
        print(f"Notification error: {e}")

    return jsonify({"message": "Expense added", "expense_id": expense_id})



@app.get("/expenses/<int:gid>")
def get_expenses_by_group(gid):
    return jsonify(query("SELECT * FROM expenses WHERE group_id=?", (gid,)))

@app.put("/expenses/<int:id>")
def update_expense(id):
    d = request.json
    query("""UPDATE expenses SET description=?, total_amount=?, paid_by=? WHERE id=?""",
          (d["description"], d["total_amount"], d["paid_by"], id))
    return jsonify({"message": "Expense updated"})

@app.delete("/expenses/<int:id>")
def delete_expense(id):
    query("DELETE FROM expenses WHERE id=?", (id,))
    return jsonify({"message": "Expense deleted"})

@app.post("/expense_shares/<int:id>/remind")
def send_reminder(id):
    conn = sqlite3.connect(DB)
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()

    try:
        share = cur.execute("SELECT user_id, expense_id FROM expense_shares WHERE id=?", (id,)).fetchone()
        if not share:
            return jsonify({"error": "Share not found"}), 404

        debtor_id = share['user_id']
        expense_id = share['expense_id']

        expense = cur.execute("SELECT description, paid_by FROM expenses WHERE id=?", (expense_id,)).fetchone()
        if not expense:
            return jsonify({"error": "Expense not found"}), 404

        creditor_id = expense['paid_by']
        description = expense['description']

        creditor = cur.execute("SELECT name, lastname FROM users WHERE id=?", (creditor_id,)).fetchone()
        creditor_name = f"{creditor['name']} {creditor['lastname']}" if creditor else "Someone"

        amount = cur.execute("SELECT amount_owed FROM expense_shares WHERE id=?", (id,)).fetchone()['amount_owed']

        send_push_notification(
            debtor_id,
            "Payment Reminder",
            f"{creditor_name} reminds you to pay {amount}€ for '{description}'."
        )

    except Exception as e:
        print(f"Reminder error: {e}")
        return jsonify({"error": str(e)}), 500
    finally:
        conn.close()

    return jsonify({"message": "Reminder sent"})

# ---------- EXPENSE SHARES ----------
@app.post("/expense_shares")
def add_share():
    d = request.json
    query("INSERT INTO expense_shares (expense_id, user_id, amount_owed, paid) VALUES (?, ?, ?, ?)",
          (d["expense_id"], d["user_id"], d["amount_owed"], d.get("paid", 0)))
    return jsonify({"message": "Share added"})

@app.get("/expense_shares/<int:eid>")
def get_shares_by_expense(eid):
    return jsonify(query("SELECT * FROM expense_shares WHERE expense_id=?", (eid,)))

@app.put("/expense_shares/<int:id>")
def update_share(id):
    d = request.json
    conn = sqlite3.connect(DB)
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()
    share = cur.execute("SELECT user_id, expense_id FROM expense_shares WHERE id=?", (id,)).fetchone()
    cur.execute("""UPDATE expense_shares SET amount_owed=?, paid=? WHERE id=?""",
              (d["amount_owed"], d["paid"], id))
    conn.commit()
    if d.get("paid") == 1 and share:
            try:
                debtor_id = share['user_id']
                expense_id = share['expense_id']

                debtor_row = cur.execute("SELECT name, lastname FROM users WHERE id=?", (debtor_id,)).fetchone()
                debtor_name = f"{debtor_row['name']} {debtor_row['lastname']}" if debtor_row else "Someone"

                expense_row = cur.execute("SELECT description, paid_by FROM expenses WHERE id=?", (expense_id,)).fetchone()

                if expense_row:
                    creditor_id = expense_row['paid_by'] # 通知を送る相手
                    description = expense_row['description']

                    if creditor_id != debtor_id:
                        send_push_notification(
                            creditor_id,
                            "Payment Received",
                            f"{debtor_name} marked '{description}' as paid."
                        )
            except Exception as e:
                print(f"Notification error in update_share: {e}")

    conn.close()
    return jsonify({"message": "Share updated"})

@app.delete("/expense_shares/<int:id>")
def delete_share(id):
    query("DELETE FROM expense_shares WHERE id=?", (id,))
    return jsonify({"message": "Share deleted"})


# ---------- GROUP BALANCES ----------
@app.get("/groups/<int:gid>/balances")
def get_group_balances(gid):
    conn = sqlite3.connect(DB)
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()

    # 1) Όλα τα μέλη του group
    cur.execute("""
        SELECT u.id, u.name, u.lastname
        FROM users u
        JOIN group_users gu ON u.id = gu.user_id
        WHERE gu.group_id = ?
    """, (gid,))
    users = cur.fetchall()
    user_ids = [u["id"] for u in users]

    net = {uid: 0.0 for uid in user_ids}

    # 2) Όλα τα shares για τα expenses του group
    cur.execute("""
        SELECT
            es.user_id AS debtor_id,
            es.amount_owed,
            e.paid_by AS payer_id
        FROM expense_shares es
        JOIN expenses e ON es.expense_id = e.id
        WHERE e.group_id = ?
          AND es.paid = 0
    """, (gid,))

    for row in cur.fetchall():
        debtor = row["debtor_id"]
        payer = row["payer_id"]
        amount = row["amount_owed"]

        if debtor not in net:
            net[debtor] = 0.0
        if payer not in net:
            net[payer] = 0.0

        net[debtor] -= amount
        net[payer] += amount

    # 3) Debtors / creditors
    debtors = []
    creditors = []

    for uid, balance in net.items():
        if abs(balance) < 0.01:
            continue
        if balance > 0:
            creditors.append({"user_id": uid, "amount": balance})
        else:
            debtors.append({"user_id": uid, "amount": -balance})

    # 4) Greedy balancing
    settlements = []
    i = j = 0

    while i < len(debtors) and j < len(creditors):
        d = debtors[i]
        c = creditors[j]

        pay = min(d["amount"], c["amount"])
        settlements.append({
            "from": d["user_id"],
            "to": c["user_id"],
            "amount": round(pay, 2)
        })

        d["amount"] -= pay
        c["amount"] -= pay

        if d["amount"] <= 0.01:
            i += 1
        if c["amount"] <= 0.01:
            j += 1

    result = {
        "net_balances": [
            {
                "user_id": u["id"],
                "name": f"{u['name']} {u['lastname']}",
                "balance": round(net[u["id"]], 2)
            }
            for u in users
        ],
        "settlements": settlements
    }

    conn.close()
    return jsonify(result)

@app.post("/groups/<int:gid>/settle")
def settle_group_debts(gid):
    print(f"--- DEBUG: Settle request received for Group {gid} ---") # ★ログ開始
    d = request.json
    user_id = d["user_id"]
    print(f"--- DEBUG: User ID from request: {user_id} ---") # ★ログ

    conn = sqlite3.connect(DB)
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()

    # Βάζουμε paid = 1 σε ΟΛΑ τα shares αυτού του user στο συγκεκριμένο group
    cur.execute("""
        UPDATE expense_shares
        SET paid = 1
        WHERE user_id = ?
        AND paid = 0
        AND expense_id IN (
          SELECT id FROM expenses WHERE group_id = ?
        )
    """, (user_id, gid))


    conn.commit()

    try:
        payer_row = cur.execute("SELECT name, lastname FROM users WHERE id=?", (user_id,)).fetchone()
        if payer_row:
            payer_name = f"{payer_row['name']} {payer_row['lastname']}"
            print(f"--- DEBUG: Payer Name found: {payer_name} ---")
        else:
            payer_name = "Someone"
            print(f"--- DEBUG: Payer Name NOT found for ID {user_id} ---")

        group_row = cur.execute("SELECT name FROM groups WHERE id=?", (gid,)).fetchone()
        if group_row:
            group_name = group_row['name']
            print(f"--- DEBUG: Group Name found: {group_name} ---")
        else:
            group_name = "the group"
            print(f"--- DEBUG: Group Name NOT found for ID {gid} ---")

        members = cur.execute("SELECT user_id FROM group_users WHERE group_id=?", (gid,)).fetchall()
        print(f"--- DEBUG: Total members found in group: {len(members)} ---")

        for m in members:
            target_uid = m['user_id']
            print(f"--- DEBUG: Checking member {target_uid}... ---")

            if target_uid != user_id:
                print(f"--- DEBUG: Attempting to send notification to User {target_uid} ---") # ★ログ
                send_push_notification(
                    target_uid,
                    "Debt Settled",
                    f"{payer_name} has settled all their debts in '{group_name}'."
                )
            else:
                print(f"--- DEBUG: Skipped User {target_uid} (Self) ---")

    except Exception as e:
        print(f"--- DEBUG ERROR (Notification Logic): {e} ---")

    conn.close()

    return jsonify({"message": "Group debts settled", "user_id": user_id, "group_id": gid})



if __name__ == "__main__":
    app.run(debug=True)
