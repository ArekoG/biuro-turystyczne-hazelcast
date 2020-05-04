# biuro-turystyczne-hazelcast/aerospike
Biuro turystyczne - składy:hazelcast, aerospike

Link do dokumentacji aerospike'a: https://www.aerospike.com/docs/client/java/start

Do uruchomienia potrzeba:
- .jar do netty'ego
- .jar do clienta aerospika
- postawienie lokalnie serwera Aerospika - w dokumentacji jest opisane jak to zrobić
W przypadku aerospike - jeśli serwer wygeneruje się pod innym adresem, można go zmienić wartość tego adresu w klasie Constants (zmienna IP_ADDRESS)
Aplikacja pozwala na wybór implementacji interesującego nas składu.

Potrzebne było obejscie do aerospike'a jeśli chodzi o zaawansowane szukanie.
Z racji że przechowuje obiekty jako bloby to nie jest możliwe założenie indexu
służącego do wyszukiwania obiektu(indeksy można zakładać jedynie na pola typu String i Numeric), dlatego też struktura rekordu ma postać:
- pole1
- pole2
- pole3
...
- obiektJakoBlob

Dzięki czemu mogę zakładać indexy na pola i dzięki temu wyszukiwać interesujace mnie obiekty. Przyklad stworzenia
indexu:
- IndexTask indexTask = client.createIndex(null, "test", "trav", "idx_destination", "destination", IndexType.STRING);
indexTask.waitTillComplete(150);

Timestamp użyty ponieważ hazelcast ma problemy z wyszukiwaniem po LocalDate.


Operacje przetwarzania danych:
1. Wyliczanie ceny podróży

2 .Pobieranie najnowszych statystyk dotyczących podróży czyli np najchętniej odwiedzane miasto, średnia ilość wydawanych pieniedzy
oraz średnia ilość spędzanych dni na podróży biorać pod uwagę najnowsze możliwe dane(nawa mapa/set `statistic`). 
Jest zaimplementowany mechanizm, który sprawdza czy zostały dodane/zaktualizowane jakieś dane. Jeśli nie to zostają pobrane najświeższe statystyki,
w przeciwnym wypadku następuje aktualizacja.