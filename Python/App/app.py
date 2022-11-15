import http.client as hc

import requests
import requests as rr

# standard
url = 'www.google.com'

connection = hc.HTTPConnection(url)
connection.request('GET', '/')
response = connection.getresponse()

print(response.read())
print(f'\n######################################################\n')

# requests
req = rr.request('GET', 'https://www.google.com').text
print(req)

# one line code
print(f'\n######################################################\n')
print(requests.request('GET', 'https://google.com').text)
