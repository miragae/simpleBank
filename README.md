# Simple Bank

**Stack technologii:**
* baza: MariaDB
* implementacja: Java Enterprise Edition + Enterprise JavaBeans
* ORM: Hibernate
* serwer aplikacyjny: Wildfly
* interfejs użytkownika: JSF + PrimeFaces
* budowanie: Maven
* uruchamianie aplikacji: Docker Compose

# Dokumentacja

## 1. Instrukcja instalacji, konfiguracji i pierwsze kroki
### 1.1 Uruchom banki dockerem
* `docker-compose up`
### 1.2 Sprawdź adresy ip banków
* `docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' wildfly_bank1`
* `docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' wildfly_bank2`
### 1.3 Zaloguj się do banku 1 i 2 (najlepiej na dwóch różnych przeglądarkach)
* `localhost:8081/SimpleBank/`
* `localhost:8082/SimpleBank/`

używając loginu:hasła
* `admin:admin`
### 1.4 Jako administrator dodaj najpierw bank lokalny

np. dla `localhost:8081/SimpleBank/`
```
name: Bank1
ip: <sprawdzony w 1.2 ip wildfly_bank1>
port: 8080
```

a następnie odpowiednio bank zewnętrzny

```
name: Bank2
ip: <sprawdzony w 1.2 ip wildfly_bank2>
port: 8080
```
### 1.5 Dodaj pracownika

np.
```
login: employee
password: employee
firstName: Jan
lastName: Kowalski
idNumber: ABC12345
type: EMPLOYEE
```

### 1.6 Dodaj klienta

np.
```
login: user
password: user
firstName: Anna
lastName: Nowak
idNumber: DEF67890
type: CLIENT
```

### 1.7 Zaloguj się jako pracownik i przyjmij wpłatę od klienta

* w wyszukiwarce w `Operations` możesz wpisać początek: imienia, nazwiska albo numeru ID

### 1.8 Dodaj banki, pracownika i klienta w drugim banku
### 1.9 Wykonaj przelew
* w oknie `Transfer` przycisk `Find account number` wyszukuje konta w swojej bazie oraz dodanych bankach
* przelew da się zrealizować tylko po znalezieniu konta

## 2. Funkcjonalności

### 2.1 Admin

**Użytkownicy**
* dodawanie użytkowników (administratorów, pracowników i klientów)
* przeglądanie listy użytkowników
* sprawdzanie numerów kont, stanu konta i przynależnego banku użytkownika

**Banki**
* dodawanie banków
* przeglądanie banków

**Konta**
* przeglądanie listy kont (ze stanem, właścicielem i bankiem)

### 2.2 Pracownik

**Klienci**
* przeglądanie listy klientów
* dodawanie nowych klientów (razem z automatycznym wygenerowaniem konta)

**Operacje**
* przyjmowanie wpłat i wypłat od klientów
* realizowanie przelewów dla klientów

### 2.3 Klient

**Stan konta**
* sprawdzenie numeru i stanu konta

**Historia**
* sprawdzenie historii transakcji na rachunku

**Przelewy**
* wykonywanie przelewów na inne konta (w tym lub innym banku)
