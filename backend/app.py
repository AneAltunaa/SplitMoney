from flask import Flask, request, jsonify
import sqlite3
import firebase_admin
from firebase_admin import credentials, messaging
import os

# --- FCM 初期化 ---
# すでに初期化されているか確認（リロード時のエラー防止）
if not firebase_admin._apps:
    cred_path = "serviceAccountKey.json"
    # ファイルの存在確認
    if os.path.exists(cred_path):
        try:
            cred = credentials.Certificate(cred_path)
            firebase_admin.initialize_app(cred)
            print("✅ SUCCESS: Firebase Admin Initialized Successfully")
        except Exception as e:
            print(f"❌ ERROR: Failed to initialize Firebase: {e}")
    else:
        print(f"❌ WARNING: '{cred_path}' not found! Notifications will NOT work.")

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

    # --- DBマイグレーション ---
def init_fcm_column():
    try:
        conn = sqlite3.connect(DB)
        # usersテーブルにfcm_tokenカラムがない場合のみ追加
        try:
            conn.execute("ALTER TABLE users ADD COLUMN fcm_token TEXT")
            print("Added fcm_token column to users table")
        except sqlite3.OperationalError:
            pass # 既に存在する場合は無視
        conn.commit()
        conn.close()
    except Exception as e:
        print(f"Migration error: {e}")

# 起動時に実行
init_fcm_column()

# --- 通知送信ヘルパー関数 ---
def send_push_notification(user_id, title, body):
    try:
        # 1. ユーザーのトークンを取得
        user = query("SELECT fcm_token FROM users WHERE id=?", (user_id,), one=True)
        if not user or not user["fcm_token"]:
            print(f"No token found for user {user_id}")
            return

        # 2. 送信
        message = messaging.Message(
            notification=messaging.Notification(title=title, body=body),
            token=user["fcm_token"],
        )
        response = messaging.send(message)
        print(f"Sent notification to user {user_id}: {response}")

    except Exception as e:
        print(f"Error sending notification: {e}")

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

# ★ トークン登録API
@app.put("/users/<int:id>/token")
def update_fcm_token(id):
    d = request.json
    token = d.get("token")
    if token:
        query("UPDATE users SET fcm_token=? WHERE id=?", (token, id))
        return jsonify({"message": "Token updated"})
    return jsonify({"error": "Token missing"}), 400

# ★ 催促通知API
@app.post("/notifications/remind")
def send_reminder():
    d = request.json
    target_user_id = d.get("target_user_id")

    if target_user_id:
        send_push_notification(target_user_id, "Payment Reminder", "Please settle your debts in the group.")
        return jsonify({"message": "Reminder sent"})
    return jsonify({"error": "Missing target_user_id"}), 400

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
        # ★追加: グループ参加通知
        send_push_notification(uid, "New Group", f"You were added to {d['name']}")

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

    # ★追加: 参加通知
    group = query("SELECT name FROM groups WHERE id=?", (d["group_id"],), one=True)
    group_name = group["name"] if group else "a group"
    send_push_notification(d["user_id"], "New Group", f"You were added to {group_name}")

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
    query("INSERT INTO expenses (group_id, description, total_amount, paid_by) VALUES (?, ?, ?, ?)",
          (d["group_id"], d["description"], d["total_amount"], d["paid_by"]))

    # ★追加: メンバーへの通知
    members = query("SELECT user_id FROM group_users WHERE group_id=?", (d["group_id"],))
    payer = query("SELECT name FROM users WHERE id=?", (d["paid_by"],), one=True)
    payer_name = payer["name"] if payer else "Someone"

    for member in members:
        uid = member["user_id"]
        if uid != d["paid_by"]: # 自分以外に送る
            send_push_notification(uid, "New Expense", f"{payer_name} added: {d['description']} (€{d['total_amount']})")

    return jsonify({"message": "Expense added"})

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
    query("""UPDATE expense_shares SET amount_owed=?, paid=? WHERE id=?""",
          (d["amount_owed"], d["paid"], id))
    return jsonify({"message": "Share updated"})

@app.delete("/expense_shares/<int:id>")
def delete_share(id):
    query("DELETE FROM expense_shares WHERE id=?", (id,))
    return jsonify({"message": "Share deleted"})

if __name__ == "__main__":
    app.run(debug=True)
