import http.client

url = 'www.google.com'

con = http.client.HTTPConnection(url)
con.request('GET', '/')
res = con.getresponse()
print(res.read())
