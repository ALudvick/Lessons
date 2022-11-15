import requests as rr
import re
import json
import config


# 1. Сформировать вызов API на поиск координат по названию 4 городов (Москва, Астана, Тель-Авив, Санкт-Петербург) -> результат - это координаты (lat и lon)
# 2. Из полученного ответа координаты (lat и lon) подставить в вызов API погоды и получить температуру по этим 4 городам

# https://api.openweathermap.org/data/3.0/onecall?lat=33.44&lon=-94.04&exclude=hourly,daily&appid={API key}


req = rr.request('GET', 'https://api.openweathermap.org/data/2.5/weather?lat=44.34&lon=10.99&appid=' + config.TOKEN).text
# print(req)

# result = re.search("\"temp\":(.*?),", req)
# print('Тепмература: ' + result.group(1))

main_param = json.loads(req)
temp_param = json.loads(str(main_param["main"]).replace('\'', '"'))
print('Тепмература: ' + str(temp_param["temp"]))

# result = re.search("\"temp\":[0-9\.]+,", req)
# print('Тепмература: ' + result.group().replace("\"temp\":", ""))

