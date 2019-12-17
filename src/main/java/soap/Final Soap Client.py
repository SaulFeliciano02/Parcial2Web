from zeep import Client

client = Client('http://localhost:7777/ws/URLWebServiceImpl?wsdl')

while True:
    option = input("1- Ver urls \n"
                   "2- Acortar url \n"
                   "3- Salir \n"
                   "Eliga la opción deseada: ")
    if option is '1':
        username = input("Indique usuario: ")
        result = client.service.getUrlsFromUser(username)
        print(result)
    if option is '2':
        username = input("Indique usuario: ")
        url = input("Indique la url a acortar: ")
        result = client.service.shortUrl(url, username)
        print("La nueva url es: " + str(result))
    if option is '3':
        break