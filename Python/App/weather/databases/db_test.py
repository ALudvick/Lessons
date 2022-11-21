import sqlite3
import time


def simple_insert_method(students):
    for st in students:
        first_insert = "INSERT INTO students (register_time, first_name, second_name, nickname, birthday) " \
                      "VALUES (" + str(st[0]) + ", '" + st[1] + "', '" + st[2] + "', '" + st[3] + "', '" + st[4] + "')"
        cur.execute(first_insert)
        conn.commit()


if __name__ == '__main__':
    conn = sqlite3.connect('test.db')
    cur = conn.cursor()

    create_student_table = """CREATE TABLE IF NOT EXISTS students(
    student_id INTEGER PRIMARY KEY AUTOINCREMENT,
    register_time INTEGER,
    first_name TEXT,
    second_name TEXT,
    nickname TEXT,
    birthday TEXT);
    """
    cur.execute(create_student_table)

    create_lecture_table = """CREATE TABLE IF NOT EXISTS lectures(lecture_id INTEGER PRIMARY KEY AUTOINCREMENT, 
        student_id INTEGER,
        lecture_title TEXT,
        lecture_date TEXT,
        FOREIGN KEY (student_id) REFERENCES students (student_id));
        """
    cur.execute(create_lecture_table)

    _students = [
        [time.time(), "Aleksandr", "Agalarov", "Ludvick", "1988-03-05 00:00:00.000"],
        [time.time(), "Sergey", "Zizdok", "ZiS", "1988-04-23 00:00:00.000"],
        [time.time(), "Igor", "Dronov", "Pentaigor1999", "1990-09-22 00:00:00.000"]
    ]

    simple_insert_method(_students)

    select_current_student = """
    SELECT first_name, birthday FROM students;
    """

    cur.execute(select_current_student)
    print(cur.fetchall())
