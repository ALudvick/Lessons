import sqlite3


class Manipulator:
    _host = None
    _port = None
    _conn = None
    _cur = None

    def __init__(self, *args):
        if len(args) == 1:
            self._host = str(args[0])
        elif len(args) == 2:
            self._host = str(args[0])
            self._port = int(args[1])
        else:
            raise Exception('Неверное количество аргументов')

    def __str__(self) -> str:
        return f'{self.__class__}\nHOST: {self._host}\nPORT:{self._port}\n~~~~~~~~~~~~~~~~'

    def connect_to_sqlite_db(self):
        try:
            print(self._host)
            self._conn = sqlite3.connect(self._host)
            self._cur = self._conn.cursor()
            print('Connection to SQLite has been established.')
            print('Try to create all tables...')
            print(f'Result: {self._create_tables()}')
        except sqlite3.Error as err:
            print(err)

    def _create_tables(self) -> int:
        queries = [
            'PRAGMA foreign_keys=on;',

            'CREATE TABLE IF NOT EXISTS CITIES (city_id integer primary key, city_name text, cord_lat realm, '
            'cord_lon real);',

            'CREATE TABLE IF NOT EXISTS USERS (register_date text, user_id integer primary key, user_name text, '
            'user_role text)',

            'CREATE TABLE IF NOT EXISTS USER_QUERIES (log_id INTEGER PRIMARY KEY AUTOINCREMENT, query_id text NOT '
            'NULL, query_date text, answer_time INTEGER, city_id INTEGER, user_id INTEGER, user_query text, '
            'response text, result_code INTEGER, FOREIGN KEY (city_id) REFERENCES cities (city_id), FOREIGN KEY ('
            'user_id) REFERENCES users (user_id));',

            'INSERT OR IGNORE INTO CITIES(city_id, city_name, cord_lat, cord_lon) VALUES (-1, \'UNKNOWN\', 0, 0)'
        ]

        try:
            for q in queries:
                self._cur.execute(q)
            print('All tables created')
            return 1
        except sqlite3.Error as err:
            print(err)
            return -666

    def add_city(self, city_id, city_name, lat, lon):
        try:
            self._cur.execute('INSERT OR IGNORE INTO cities (city_id, city_name, cord_lat, cord_lon) '
                              'VALUES (?, ?, ?, ?);', (city_id, city_name, lat, lon))
            self._conn.commit()
        except sqlite3.Error as err:
            print(f'add_city() error: {err}')

    def add_user(self, register_date, user_id, user_name, user_role):
        try:
            self._cur.execute('INSERT OR IGNORE INTO USERS (register_date, user_id, user_name, user_role) '
                              'VALUES (?, ?, ?, ?);', (register_date, user_id, user_name, user_role))
            self._conn.commit()
        except sqlite3.Error as err:
            print(f'add_user() error: {err}')

    def add_query(self, query_id, query_date, answer_time, city_id, user_id, user_query, response, result_code):
        try:
            self._cur.execute('INSERT INTO USER_QUERIES (query_id, query_date, answer_time, city_id, '
                              'user_id, user_query, response, result_code) '
                              'VALUES (?, ?, ?, ?, ?, ?, ?, ?);',
                              (query_id, query_date, answer_time, city_id, user_id, user_query, response,
                               result_code))
            self._conn.commit()
        except sqlite3.Error as err:
            print(f'add_query() error: {err}')
