# biuro-turystyczne-hazelcast/aerospike
Biuro turystyczne - składy:hazelcast, aerospike

Link do dokumentacji aerospike'a: https://www.aerospike.com/docs/client/java/start

Do uruchomienia potrzeba:
- .jar do netty'ego
- .jar do clienta aerospika
- postawienie lokalnie serwera Aerospika - w dokumentacji jest opisane jak to zrobić
Aplikacja pozwala na wybór implementacji interesującego nas składu

Potrzebne było obejscie do aerospike jestli chodzi o zaawansowane szukanie.
Z racji że przechowuje obiekty jako bloby to nie jest możliwe założenie indexu
służącego do wyszukiwania obiektu(indeksy można zakładać jedynie na pola typu String i Numeric), dlatego też struktura rekordu ma postać:
- pole1
- pole2
- pole3
...
- obiektJakoBlob

Dzięki czemu mogę zakładać indexy na pola i dzięki temu wyszukiwać interesujace mnie obiekty. Przyklad stworzenia
indexu:
- IndexTask indexTask = client.createIndex(null, "test", "travel", "idx_dest", "destination", IndexType.STRING);
indexTask.waitTillComplete(150);

Timestamp użyty ponieważ hazelcast ma problemy z wyszukiwaniem po LocalDate np


Operacje przetwarzania danych:
1. Wyliczanie ceny podróży
2. Pobieranie najnowszych statystyk dotyczących podróży czyli np najchętniej odwiedzane miasto, średnia ilość wydawanych pieniedzy
oraz średnia ilość spędzanych dni na podróży biorać pod uwagę najnowsze możliwe dane(nawa mapa/set `statistic`)