
# Projekt: Darwin World

## Autorzy

- **Tomasz Głąbek** (a.k.a. [memecat5](https://github.com/memecat5))
- **Maciej Kmąk** (a.k.a. [Ki3mONo](https://github.com/Ki3mONo))

## Opis projektu

**Darwin World** to symulacja, która umożliwia obserwację procesu ewolucji w wirtualnym świecie. Celem jest stworzenie gry, w której zwierzęta, rośliny i różnorodne zmienne środowiskowe współdziałają. Zwierzęta będą poruszać się po mapie, jedząc rośliny, rozmnażając się i ewoluując w odpowiedzi na warunki otoczenia. 

Wariant, który został nam przydzielony to F-4: `Pełzająca dżungla` i `Starość nie radość`. Więcej o wariantach [tutaj](#instrukcje)

## Informacje o projekcie
Projekt został zrealizowany w ramach przedmiotu **Programowanie Obiektowe** na II roku studiów na kierunku **Informatyka** na **Wydziale Informatyki Akademii Górniczo-Hutniczej**.

## Instrukcje
Więcej szczegółów dotyczących projektu oraz instrukcje można znaleźć w katalogu [Tresc_zadania](https://github.com/Ki3mONo/DarwinWorldSimulator/blob/main/Tresc_zadania/Readme.md).


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
