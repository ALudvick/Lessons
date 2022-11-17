import requests as req
import json
import config

LIMIT = '15'


def get_json_by_city_name(city_name):
    _user_request = req.request('GET', 'http://api.openweathermap.org/geo/1.0/direct?'
                                       'q=' + city_name +
                                       '&limit=' + LIMIT +
                                       '&appid=' + config.TOKEN
                                ).text

    json_object = json.loads(_user_request)

    print('Пожалуйста, конкретизируйте ваш выбор: ')
    element_number = 1
    for element in json_object:
        print(f'{element_number}: {element["name"]} - {element["country"]} - {element["state"]} ({element["lat"]} : {element["lon"]})')
        element_number += 1

    return json_object[get_user_choice(len(json_object)) - 1]


def get_weather_by_coordinates(lat, lon):
    _user_request = req.request('GET', 'https://api.openweathermap.org/data/2.5/weather?'
                                       'lat=' + str(lat) +
                                       '&lon=' + str(lon) +
                                       '&appid=' + config.TOKEN +
                                       '&units=metric'
                                ).text

    main_param = json.loads(_user_request)
    temp_param = json.loads(str(main_param["main"]).replace('\'', '"'))
    print(f'Тепмература: {str(temp_param["temp"])}C°. Ощущается как: {str(temp_param["feels_like"])}C°.')


def get_user_choice(json_size):
    _user_choice = int(input('\nВыбор: '))
    if 0 < _user_choice <= json_size:
        return _user_choice
    else:
        print('К сожалению, я не понял ваш выбор или была допущена ошибка. Пожалуйста, повторите снова')
        return get_user_choice(json_size)


if __name__ == '__main__':
    _city_name = input('Введите название города: ')
    current_city_json = get_json_by_city_name(_city_name)
    _lat = current_city_json["lat"]
    _lon = current_city_json["lon"]
    get_weather_by_coordinates(_lat, _lon)
