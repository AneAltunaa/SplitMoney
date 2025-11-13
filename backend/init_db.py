import sqlite3

conn = sqlite3.connect("database.db")
cursor = conn.cursor()

cursor.execute("DROP TABLE IF EXISTS expense_shares")
cursor.execute("DROP TABLE IF EXISTS expenses")
cursor.execute("DROP TABLE IF EXISTS group_users")
cursor.execute("DROP TABLE IF EXISTS groups")
cursor.execute("DROP TABLE IF EXISTS users")

# Users table
cursor.execute("""
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    lastname TEXT NOT NULL,
    mail TEXT NOT NULL,
    phone TEXT NOT NULL,
    password TEXT NOT NULL
)
""")

# Groups table
cursor.execute("""
CREATE TABLE IF NOT EXISTS groups (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT NOT NULL
)
""")

# Groups-Users
cursor.execute("""
CREATE TABLE IF NOT EXISTS group_users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    group_id INTEGER,
    user_id INTEGER,
    FOREIGN KEY (group_id) REFERENCES groups(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE(group_id, user_id)
)
""")

# Expenses table
cursor.execute("""
CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    group_id INTEGER,
    description TEXT NOT NULL,
    total_amount REAL NOT NULL,
    paid_by INTEGER,
    FOREIGN KEY (group_id) REFERENCES groups(id),
    FOREIGN KEY (paid_by) REFERENCES users(id)
)
""")

# Expense shares table
cursor.execute("""
CREATE TABLE IF NOT EXISTS expense_shares (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    expense_id INTEGER,
    user_id INTEGER,
    amount_owed REAL NOT NULL,
    paid INTEGER DEFAULT 0,  -- 0 = no, 1 = yes
    FOREIGN KEY (expense_id) REFERENCES expenses(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
)
""")

# Example inserts
users = [
    ("Alice", "Smith", "alice@example.com", "123456789", "password"),
    ("Bob", "Johnson", "bob@example.com", "234567890", "1234"),
    ("Charlie", "Brown", "charlie@example.com", "345678901", "asdfg"),
    ("David", "Lee", "david@example.com", "456789012", "a")
]
cursor.executemany("INSERT INTO users (name, lastname, mail, phone, password) VALUES (?, ?, ?, ?, ?)", users)

groups = [
    ("Copenhagen Trip", "Weekend trip to Copenhagen"),
    ("Oslo Trip", "Winter trip to Oslo"),
    ("Stockholm Trip", "Holiday trip to Stockholm")
]
cursor.executemany("INSERT INTO groups (name, description) VALUES (?, ?)", groups)

group_users = [
    (1, 1), (1, 2), (1, 3),
    (2, 1), (2, 2), (2, 4),
    (3, 1), (3, 3), (3, 4)
]
cursor.executemany("INSERT INTO group_users (group_id, user_id) VALUES (?, ?)", group_users)

expenses = [
    (1, "Hotel", 300, 1),
    (1, "Dinner", 150, 2),
    (1, "Museum Tickets", 90, 3),
    (1, "Transport", 60, 1),
    (2, "Hotel", 400, 1),
    (2, "Ski Pass", 200, 2),
    (2, "Dinner", 180, 4),
    (2, "Transport", 80, 1),
    (3, "Hotel", 350, 1),
    (3, "Dinner", 120, 3),
    (3, "Boat Tour", 90, 4),
    (3, "Museum Tickets", 60, 1)
]
cursor.executemany("INSERT INTO expenses (group_id, description, total_amount, paid_by) VALUES (?, ?, ?, ?)", expenses)

expense_shares = [
    (1, 1, 100, 0), (1, 2, 100, 0), (1, 3, 100, 0),
    (2, 1, 50, 0), (2, 2, 50, 0), (2, 3, 50, 0),
    (3, 1, 45, 0), (3, 2, 45, 0),
    (4, 1, 30, 0), (4, 2, 15, 0), (4, 3, 15, 0),
    (5, 1, 133.33, 0), (5, 2, 133.33, 0), (5, 4, 133.34, 0),
    (6, 1, 100, 0), (6, 2, 100, 0), (6, 4, 0, 0),
    (7, 1, 60, 0), (7, 2, 60, 0), (7, 4, 60, 0),
    (8, 1, 40, 0), (8, 2, 20, 0), (8, 4, 20, 0),
    (9, 1, 116.67, 0), (9, 3, 116.66, 0), (9, 4, 116.67, 0),
    (10, 1, 40, 0), (10, 3, 40, 0),
    (11, 1, 30, 0), (11, 3, 30, 0), (11, 4, 30, 0),
    (12, 1, 20, 0), (12, 3, 20, 0), (12, 4, 20, 0)
]
cursor.executemany("INSERT INTO expense_shares (expense_id, user_id, amount_owed, paid) VALUES (?, ?, ?, ?)", expense_shares)

conn.commit()
conn.close()
