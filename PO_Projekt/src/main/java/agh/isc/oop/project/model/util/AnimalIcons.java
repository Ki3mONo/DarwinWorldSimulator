package agh.isc.oop.project.model.util;

/**
 * Enum reprezentujący możliwe obrazki dla zwierzaka.
 */
public enum AnimalIcons {
    BOCIAN, KURCZAK, KRUK, PINGWIN, KURA, SOWONIEDZWIEDZ;

    /**
     * Metoda zwracająca ścieżkę do danej ikony
     * @return ścieżka do ikony zwierzaka
     */
    public String getRecourseName() {
        return "/animals/" + switch (this) {
            case BOCIAN -> "bocian.png";
            case KURCZAK -> "kurczak.png";
            case KRUK -> "kruk.png";
            case PINGWIN -> "pingwin.png";
            case KURA -> "kura.png";
            case SOWONIEDZWIEDZ -> "sowoniedz.png";
        };
    }
}
