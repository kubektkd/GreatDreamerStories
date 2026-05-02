# Brief Scenariusza - Fabuła początkowej historii

## 1. Informacje Metadane
*   **Wstępne info:** Historia ta jest punktem początkowym całej gry. Po ukończeniu tego scenariusza historia postaci gracza będzie mogła być dalej rozwijana w kolejnych historiach, będących rozwinięciem początkowych decyzji. Każda historia/scenariusz będzie osobną rozgrwyką, jednocześnie wszyskie pojedyncze historie będą składały się w jedną większą całość. 
*   **Tytuł scenariusza:** Druga
*   **Gatunek:** Nordic-noir, horror psychologiczny, mystery
*   **Miejsce akcji:** Stoksjö, odizolowane miasteczko w Skandynawii (prowincja Helbjerg), otoczone wzgórzami i ciemnymi lasami
*   **Czas akcji:** Luty 2020 roku, kilka dni przed walentynkami, okres surowej zimy
*   **Główny motyw:** Pakt z nadnaturalną istotą, podmiana dzieci (odmieniec), śledztwo w zamkniętej społeczności skrywającej bardzo starą tajemnicę

## 2. Świat Gry i Lore (Wiedza dla AI)
*   **Piaskun (Sandman):** Pradawna istota (humanoidalna postać starca mająca 3 m wzrostu, maska z czaszki jelenia), żyjąca na pograniczu rzeczywistości i krainy koszmarów.
*   **Pakt:** Mieszkańcy Stoksjö od pokoleń zawierają pakty z Piaskunem: w zamian za pomyślność miasteczka oddają mu jedno dziecko, a on podrzuca **Odmieńca** (swojej krwi), który zajmuje miejsce oddanego dziecka.
*   **Wydarzenie sprzed 15 lat:** Podczas epidemii grypy dwuletnia Branja Madsen została wykradziona przez czonkinię starszyzny (Hildę Krogg) i oddana Piaskunowi. Na jej miejsce podłożono Odmieńca, który dorastał w rodzinie Madsenów.
*   **Kraina Koszmarów:** Lustrzane odbicie świata rzeczywistego, gdzie mowa i fizyka są "na opak" (np. mówienie wspak).

## 3. Profile Postaci (Mechanika NPC/Gracz)

### Główny Bohater (Gracz)
*   **Rola:** Komendant lokalnego posterunku policji.
*   **Cechy (do potencjalnych mechanik gry):** Postać z przeszłością, borykająca się z co najmniej dwoma problemami: uzależnienie (alkohol/hazard), problemy finansowe, samotne rodzicielstwo lub kłopoty zdrowotne.

### Zastępca (NPC Towarzyszący)
*   **Postać:** Sven Krogg.
*   **Rola:** Wsparcie śledcze. Młody, energiczny policjant/policjantka, który nie zna mrocznej tajemnicy miasta. Wnuk Hildy Krogg.

### Kluczowi NPC
*   **Prawdziwa Branja Madsen:** Dziewczyna odnaleziona na drodze podczas śnieżycy; mówi wspak (odbicie lustrzane krainy koszmarów), ma wysoką liczbę limfocytów, odciski palców pasują do zaginionej 15 lat temu 2-latki.
*   **Odmieniec (Branja):** 17-letnia "córka" Madsenów. Zbuntowana, agresywna, ma Romans z Jakubem Marmolem, którego chce zabrać do świata Piaskuna jako niewolnika w swoje 18. urodziny (14 lutego).
*   **Jakub Marmol:** 19-letni motocyklista, emocjonalnie i magicznie uzależniony od fałszywej Branji (Odmieńca), traci wolną wolę pod wpływem jej magii.
*   **Hilda Krogg:** Miła lecz przebiegła staruszka ze starszyzny; to ona 15 lat temu dokonała podmiany dziecka. Dobrze zna tajemnicę miasteczka.
*   **Ingrid Madsen:** Matka Branji, wycieńczona energetycznie i psychicznie przez Odmieńca.
*   **Yngvar Madsen:** Ojciec Branji, wrak człowieka mieszkający w wagonie kolejowym; przeczuwa, że podmieniona przed laty córka jest "inna", ale nie ma wystarczających na to dowodów.

## 4. Główne Etapy Fabuły (Questy/Eventy)
0.  **Scena Inicjująca**: Gracz wraca nocą autem z odległego o kilkadziesiąt kilometrów większego miasta (miał tam do załatwienia sprawy służbowe) i nagle natrafia, na zasypanej świeżym śniegiem drodze, ubraną jedynie w długą halkę (piżamę), przerażoną dziewczynę (Prawdziwą Branję).
1.  **Spotkanie na drodze:** Gracz znajduje zmarzniętą dziewczynę na brzegu lasu podczas śnieżycy. Próbuje się z nią porozumieć, ale dziewczyna mówi w dziwnym języku (w rzeczywistości jest to język gracza, ale wspak).
2.  **Śledztwo techniczne:** Analiza odcisków palców (nielogiczny wynik: odciski znalezionej dziewczyny pasują do 2-latki (Branji) z bazy danych), badania medyczne (wynik: wysoki poziom limfocytów oraz ślady starych złamań), przeszukiwanie policyjnego archiwum, wizyta w miejskiej bibliotece.
3.  **Rekonstrukcja wydarzeń:** Rozmowy z Ingrid, Yngvarem i emerytowanym policjantem Stembergiem (poprzednim komendantem, za czasów którego mała Branja została podmieniona) na temat zaginięcia sprzed 15 lat.
4.  **Konfrontacja w barze:** Spotkanie Odmieńca i Jakuba w barze "Zombie Drink Machine"; próba aresztowania lub interwencji.
5.  **Finał (14 lutego):** Wydarzenia przy starej cegielni; otwarcie "drzwi" narysowanych kredą na ścianie do świata Piaskuna.

## 5. Wytyczne dla AI (Logika Gry)
*   **Mechanika Dialogu:** Jeśli postać mówi wspak (Prawdziwa Branja), AI powinno implementować odwrócenie tekstu w celach zagadek językowych.
*   **System Poczytalności/Stresu:** Manifestacje Piaskuna (sypiący się piach, latające przedmioty, pękające szyby) powinny wpływać na statystyki gracza.
*   **Relacje NPC -PSDB (Początkowy Stosunek Do Bohatera):** Implementacja początkowego nastawienia (np. Branja - negatywne, Sven Krogg - pozytywne, Hilda Krogg - neutralnie).
*   **Presja Czasu:** Akcja musi dążyć do punktu kulminacyjnego 14 lutego.
*   **Sposób na Piaskuna:** Zgodnie z lokalną legendą, jedynym sposobem na pradawną istotę jest przegnanie jej używając otwartego ognia.

## 6. Możliwe zakończenia
*   **Ocalenie**: Powstrzymanie Jakuba przed przejściem na drugą stronę.
*   **Ofiara**: Piaskun zabiera gracza lub Jakuba jako niewolnika.
*   **Konfrontacja społeczna**: Starszyzna miasteczka próbuje siłą oddać prawdziwą Branję Piaskunowi, by chronić pakt.
*   **Koszmar**: Piaskun manifestuje się w świecie rzeczywistym, co prowadzi do sceny walki/odpędzania go ogniem.

---

**Uwagi dla AI:** Przy tworzeniu kodu i dialogów należy kłaść nacisk na oniryczność, melancholię i surowość zimy. NPC powinni reagować na gracza z nieufnością typową dla zamkniętych społeczności, które skrywają mroczną tajemnicę