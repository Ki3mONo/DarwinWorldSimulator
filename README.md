
# Projekt: Darwin World

## Autorzy

- **Tomasz Głąbek** (a.k.a. [memecat5](https://github.com/memecat5))
- **Maciej Kmąk** (a.k.a. [Ki3mONo](https://github.com/Ki3mONo))

## Opis projektu

**Darwin World** to symulacja, która umożliwia obserwację procesu ewolucji w wirtualnym świecie. Gra polega na stworzeniu środowiska, w którym rośliny i zwierzęta rozwijają się i ewoluują w odpowiedzi na zmieniające się warunki. Gra ma na celu pokazanie, jak różne czynniki środowiskowe oraz mechanizmy genetyczne wpływają na przetrwanie i rozwój gatunków.

## Informacje o projekcie
Projekt został zrealizowany w ramach przedmiotu **Programowanie Obiektowe** na II roku studiów na kierunku **Informatyka** na **Wydziale Informatyki Akademii Górniczo-Hutniczej**.

## Instrukcje
Więcej szczegółów dotyczących projektu oraz instrukcje można znaleźć w katalogu [Tresc_zadania](https://github.com/Ki3mONo/DarwinWorldSimulator/blob/main/Tresc_zadania/Readme.md).

## Cel projektu

Celem jest stworzenie gry, w której zwierzęta, rośliny i różnorodne zmienne środowiskowe współdziałają. Zwierzęta będą poruszać się po mapie, jedząc rośliny, rozmnażając się i ewoluując w odpowiedzi na warunki otoczenia.

## Kluczowe elementy projektu

- **Świat gry**: Prosty, prostokątny obszar podzielony na kwadratowe pola.
- **Zwierzęta**: Roślinożercy z różnymi genotypami, które poruszają się po świecie, zbierając energię z roślin, rozmnażają się i ewoluują.
- **Genotypy zwierząt**: Zestaw genów, które kontrolują zachowanie zwierząt. Genotypy zmieniają się przez mutacje i dziedziczenie.
- **Interakcje ze środowiskiem**: Rośliny rosną na mapie, zwierzęta zbierają energię i rozmnażają się.
- **Symulacja**: Codzienne cykle, w których zwierzęta poruszają się, jedzą rośliny, rozmnażają się i mutują.

## Funkcje

- Wybór różnych wariantów mapy i wzrostu roślin.
- Możliwość śledzenia wybranych zwierząt i obserwacji ich życia.
- Zapis statystyk symulacji do pliku CSV.
- Graficzny interfejs użytkownika oparty na bibliotece **JavaFX**.

## Implementacja

### Wybrane klasy w projekcie

1. **WorldMapFactory**
    - Tworzy mapy różnych typów, w tym `EquatorForestMap` oraz `CrawlingJungleMap` na podstawie dostarczonego typu mapy i rozmiaru.

2. **WorldMap**
    - Interfejs reprezentujący mapę. Zawiera metody do sprawdzania, czy można poruszać się po danym polu, umieszczania zwierząt na mapie oraz zarządzania obiektami na mapie.

3. **WorldElementBox**
    - Klasa odpowiedzialna za wizualizację elementów świata, takich jak zwierzęta i rośliny, na mapie. Używa komponentów JavaFX takich jak `ProgressBar` do wyświetlania stanu zdrowia zwierząt.

4. **Vector2d**
    - Reprezentuje współrzędne w przestrzeni 2D. Oferuje różne operacje matematyczne, takie jak dodawanie, odejmowanie czy porównywanie wektorów.

5. **Animal**
    - Klasa reprezentująca zwierzę. Zawiera informacje o jego pozycji, energii, genomie, a także metody poruszania się, jedzenia roślin i rozmnażania.

6. **Genome**
    - Reprezentuje genom zwierzęcia. Przechowuje listę genów oraz odpowiada za logikę aktywowania kolejnych genów w trakcie symulacji.

7. **AbstractWorldMap**
    - Abstrakcyjna klasa mapy, która zarządza umieszczaniem zwierząt i roślin na mapie oraz obsługuje logikę związaną z ruchem, rozmnażaniem i wzrostem roślin.

8. **AnimalFactory**
    - Klasa fabryki do tworzenia nowych zwierząt, zarówno początkowych, jak i urodzonych podczas symulacji.

9. **Simulation**
    - Klasa odpowiedzialna za uruchamianie symulacji, cykliczne zmiany dnia, zarządzanie mapą i interakcje między zwierzętami.

10. **SimulationMapWindowController**
    - Kontroler okna symulacji, który zarządza wyświetlaniem mapy, aktualizacją statystyk oraz interakcjami użytkownika, takimi jak zatrzymywanie, wznawianie symulacji, a także śledzenie zwierząt.

11. **SimulationEngine**
    - Silnik symulacji zarządzający wieloma równoległymi symulacjami, pozwala na uruchamianie, pauzowanie i wznawianie symulacji.

12. **SimulationApp**
    - Główna aplikacja JavaFX, która zarządza oknem głównym i umożliwia użytkownikowi wybór pliku konfiguracyjnego lub rozpoczęcie nowej symulacji.

### Kluczowe komponenty

- **Wzrost roślin**: Rośliny rosną na mapie na podstawie zadanych reguł i konfiguracji, z losowym rozmieszczeniem w zależności od typu mapy.
- **Poruszanie zwierząt**: Zwierzęta poruszają się zgodnie z aktywowanymi genami, zużywając energię przy każdym ruchu i zbierając rośliny w celu utrzymania energii.
- **Rozmnażanie**: Zwierzęta rozmnażają się, gdy ich energia osiągnie odpowiedni poziom, a ich potomkowie dziedziczą część genomu rodziców, z możliwością mutacji.
- **Symulacja**: Cykl symulacji obejmuje codzienne zmiany na mapie, poruszanie zwierząt, wzrost roślin, a także rozmnażanie i umieranie zwierząt. Statystyki są aktualizowane i wyświetlane w interfejsie użytkownika.

### Interfejs użytkownika

- Graficzny interfejs użytkownika wykorzystuje bibliotekę **JavaFX** do wyświetlania mapy, zwierząt, roślin oraz statystyk.
- Symulację można zatrzymać, wznowić, a także śledzić szczegóły życia poszczególnych zwierząt.

## Technologie

- **JavaFX**: Użyte do stworzenia interfejsu użytkownika.
- **Gradle**: Narzędzie do budowania i uruchamiania aplikacji.
