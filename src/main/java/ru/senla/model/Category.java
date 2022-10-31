package ru.senla.model;

public enum Category {
    ESTATE("Недвижимость"),
    TRANSPORT("Транспорт"),
    WORK("Работа"),
    SERVICE("Услуга"),
    HOUSEHOLD("Товары для дома"),
    CLOTHES("Одежда и аксессуары"),
    ELECTRONICS("Электроника");

    private final String name;

    Category(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
