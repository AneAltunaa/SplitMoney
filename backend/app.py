from flask import Flask, request, jsonify
import sqlite3

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

# ---------- GROUPS ----------
@app.post("/groups")
def create_group():
    d = request.json
    query("INSERT INTO groups (name, description) VALUES (?, ?)", (d["name"], d["description"]))
    gid = query("SELECT last_insert_rowid() AS id", one=True)["id"]
    for uid in d.get("participants", []):
        query("INSERT OR IGNORE INTO group_users (group_id, user_id) VALUES (?, ?)", (gid, uid))
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
    query("INSERT INTO expenses (group_id, description, total_amount, paid_by) VALUES (?, ?, ?, ?)",
          (d["group_id"], d["description"], d["total_amount"], d["paid_by"]))
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
