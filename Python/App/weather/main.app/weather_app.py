from datetime import datetime
import time
import uuid
import requests as req
import json
import config
import user
import database_manipulator

LIMIT = '15'
global db_manipulator
global guest


def get_json_by_city_name(city_name):
    _start = time.time()
    _user_request = req.request('GET', 'http://api.openweathermap.org/geo/1.0/direct?'
                                       'q=' + city_name +
                                       '&limit=' + LIMIT +
                                       '&appid=' + config.TOKEN
                                ).text

    json_object = json.loads(_user_request)

    _end = time.time()
    db_manipulator.add_query(guest.query_id, datetime.now(), (_end - _start), -1,
                             guest.user_id, guest.message, str(json_object), 1)

    if len(json_object) != 0:
        print('Пожалуйста, конкретизируйте ваш выбор: ')
        element_number = 1
        for element in json_object:
            print(f'{element_number}: {element["name"]} - {element["country"]} - {element["state"]} '
                  f'({element["lat"]} : {element["lon"]})')
            element_number += 1

        return json_object[get_user_choice(len(json_object)) - 1]
    else:
        raise TypeError('Неизвестный город')


def get_weather_by_coordinates(lat, lon):
    _start = time.time()
    _user_request = req.request('GET', 'https://api.openweathermap.org/data/2.5/weather?'
                                       'lat=' + str(lat) +
                                       '&lon=' + str(lon) +
                                       '&appid=' + config.TOKEN +
                                       '&units=metric'
                                ).text

    main_param = json.loads(_user_request)

    db_manipulator.add_city(str(main_param["id"]), str(main_param["name"]), lat, lon)

    temp_param = json.loads(str(main_param["main"]).replace('\'', '"'))
    result = f'Тепмература: {str(temp_param["temp"])}C°. Ощущается как: {str(temp_param["feels_like"])}C°.'
    print(result)
    _end = time.time()
    db_manipulator.add_query(guest.query_id, datetime.now(), (_end - _start), str(main_param["id"]),
                             guest.user_id, guest.message, str(temp_param), 1)


def get_user_choice(json_size):
    error_message = 'К сожалению, я не понял ваш выбор или была допущена ошибка. Пожалуйста, повторите снова'
    _start = time.time()
    _user_input = input('\nВыбор: ')
    guest.query_id = str(uuid.uuid4())
    guest.message = _user_input

    try:
        _user_choice = int(_user_input)
        if 0 < _user_choice <= json_size:
            _end = time.time()
            db_manipulator.add_query(guest.query_id, datetime.now(), (_end - _start), -1,
                                     guest.user_id, guest.message, 'OK', 1)
            return _user_choice
        else:
            _end = time.time()
            print(error_message)
            db_manipulator.add_query(guest.query_id, datetime.now(), (_end - _start), -1,
                                     guest.user_id, guest.message, error_message, -666)
            return get_user_choice(json_size)
    except ValueError as ve:
        _end = time.time()
        print(error_message)
        db_manipulator.add_query(guest.query_id, datetime.now(), (_end - _start), -1,
                                 guest.user_id, guest.message, str(ve), -666)
        return get_user_choice(json_size)


def start_weather_app():
    db_manipulator.add_user(datetime.now(), guest.user_id, guest.user_name, guest.user_role)
    _start = time.time()
    try:
        user_city_name = input('Введите название города: ')
        guest.query_id = str(uuid.uuid4())
        guest.message = user_city_name
        return get_json_by_city_name(user_city_name)
    except TypeError as te:
        print(f'start_weather_app(): {te}')
        _end = time.time()
        db_manipulator.add_query(guest.query_id, datetime.now(), (_end - _start), -1,
                                 guest.user_id, guest.message, te, -666)
        return start_weather_app()


if __name__ == '__main__':
    guest = user.User(0, 'Ludvick', 'Admin')
    db_manipulator = database_manipulator.Manipulator('weather.db')
    db_manipulator.connect_to_sqlite_db()
    current_city_json = start_weather_app()
    _lat = current_city_json["lat"]
    _lon = current_city_json["lon"]
    get_weather_by_coordinates(_lat, _lon)
