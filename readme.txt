!----- INSTRUKCJA DO URUCHOMIENIA APLIKACJI MOBILNEJ -----!
1. Pobierz android studio. Link: https://developer.android.com/studio?hl=pl
2. Po uruchomieniu Instalatora postępuj zgodnie z krokami:
2.1 Next ->
2.2 W zakładce Choose Components zaznacz obie opcję (Android Studio oraz Android Virtual Device) -> Next
2.3 Wybierz preferowaną ścieżkę instalacji -> Next
2.4 Install
2.5 Po ukończeniu instalacji -> Next
3. Jeżeli jeszcze tego nie zrobiłeś/aś, sklonuj projekt z gita do dowolnego folderu. Link: https://github.com/PiotrNakonowski/WhizzApp
4. Uruchom aplikację Android Studio oraz postępuj zgodnie z krokami:
4.1 Do not import settings -> OK 
4.2 Help improve Android Studio -> Don't send
4.3 Aplet Welcome -> Next
4.4 Install Type -> wybieramy Standard -> Next
4.5 Verify Settings -> Next
4.6 License Agreement -> Accept (dla wszystkich licencii - może być tylko jedna "android-sdk-license" lub dwie) -> Finish
4.7 Downloading Components -> Finish
5. Wybieramy opcję Open -> Teraz wchodzimy folder gdzie sklonowaliśmy projekt -> Wybieramy folder "MobileApp" -> Trust Project
6. Czekamy, aż Gradle skończy konfigurację (prawy dolny róg)
7. Włączamy aplikację na górnym pasku (przycisk "Play")
8. Czekamy, aż emulator telefonu się uruchomi.
9. Aplikacja jest gotowa do przeglądu
10. Przykładowe konto:
Adres e-mail: pefime1902@hutov.com
Hasło: 123456

POWIADOMIENIA
Aby powiadomienia były widoczne należy zmienić uprawnienia aplikacji.
1. Przejdź do ustawień telefonu (przeciągnij ekran z dołu do góry mając kursor np. w połowie ekranu).
2. Przejdź do Apps -> WhizzApp -> Permissions -> Notifications -> Włącz opcję "All WhizzApp notifications"
3. Teraz po dodaniu zadania w liście to-do, po 10 sekundach powinno pojawić się powiadomienie.

DODANIE ZDJĘCIA DO TELEFONU
Sposób nr 1
1. Otwórz przeglądarkę chrome na telefonie.
2. Wyszukaj zdjęcie w google grafika np. kotek jpg.
3. Po kliknięciu w zdjęcie przytrzymaj i z menu rozwijalnego wybierz "Download".
Teraz podczas dodawnia zdjęcia do planu lekcji lub wydarzeń powinno być widoczne zdjęcie.

Sposób nr 2
1. Pobierz zdjęcie z Internetu na swój komputer.
2. Włącz telefon.
3. Z menu po prawej stronie wybierz "Device manager".
4. Kliknij na opcję emulatora (trzy kropki po prawej stronie).
5. Wybierz opcję "Open in Device Explorer"
6. W Device Explorer przejdź do następującego folderu: storage -> emulated -> 0 -> Pictures
7. Klinij PPM na Pictures i wybierz "Upload...".
8. Wybierz zdjęcie z dysku komputera i kliknij "OK".
9. Zresetuj telefon.
10. W przypadku kiedy zdjęcia nie widać w aplikacji -> powtórz proces.
Teraz podczas dodawnia zdjęcia do planu lekcji lub wydarzeń powinno być widoczne zdjęcie.

!----- INSTRUKCJA DO URUCHOMIENIA APLIKACJI WEBOWEJ -----!

1. Instalacja Node.js

1.1 Otwórz przeglądarke i wyszukaj https://nodejs.org/en/download
1.2 Pobierz i zainstaluj Node.js

2. Instalacja bibliotek React

2.1 Idź do folderu 'WebApp' i otworz terminal
2.2 Wpisz 'npm install' aby zainstalować odpowiednie zależności dla tej części projektu

2.3 Przejdź do katalogu 'functions'
2.4 Wpisz 'npm install' aby zainstalować odpowiednie zależności dla tej części projektu

3. Uruchomienie aplikacji
3.1 Wróć do katalogu 'WebApp'
3.2 Wpisz 'npm start' aby uruchomić aplikacje w oknie przeglądarki
