package kz.iitu.spring_lab_01.catalog;

// Variant 5: interface projection with three fields.
// Hibernate selects only these columns (see SQL in report),
// unlike the entity query which selects the whole row.
public interface BookBrief {
    Long getId();
    String getTitle();
    int getYear();
}
