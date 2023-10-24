/* (c) https://github.com/MontiCore/monticore */
package selib;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SELibServerApplication extends SELibServerApplicationTOP {
  @Override
  public void init() throws IOException {
    super.init();

    BookDescription hdr1 = SELibManager.bookDescriptionBuilder()
        .title("HDR1")
        .description("Herr Der Ringe - Die Gefaehrten")
        .isbn("123-1")
        .created(1900l)
        .imageUrl("Gefahrten")
        .genre(Genre.FANTASY)
        .build().get();
    BookDescription hdr2 = SELibManager.bookDescriptionBuilder()
        .title("HDR2")
        .description("Herr Der Ringe - Die Zwei Tuerme")
        .isbn("123-2")
        .created(1901l)
        .imageUrl("Tuemre")
        .genre(Genre.FANTASY)
        .build().get();
    BookDescription hdr3 = SELibManager.bookDescriptionBuilder()
        .title("HDR3")
        .description("Herr Der Ringe - Rueckkehr Des Koenigs")
        .isbn("123-3")
        .created(1903l)
        .imageUrl("Aragorn")
        .genre(Genre.FANTASY)
        .build().get();

    Optional<Book> hdr1Book = SELibManager.bookBuilder()
        .bookNumber(1)
        .description(hdr1).build();
    SELibManager.bookBuilder()
        .bookNumber(2)
        .description(hdr1).build();
    SELibManager.bookBuilder()
        .bookNumber(3)
        .description(hdr1).build();
    SELibManager.bookBuilder()
        .bookNumber(4)
        .description(hdr1).build();

    SELibManager.bookBuilder()
        .bookNumber(5)
        .description(hdr2).build();
    SELibManager.bookBuilder()
        .bookNumber(6)
        .description(hdr2).build();
    Optional<Book> hdr2Book = SELibManager.bookBuilder()
        .bookNumber(7)
        .description(hdr2).build();
    SELibManager.bookBuilder()
        .bookNumber(8)
        .description(hdr2).build();
    SELibManager.bookBuilder()
        .bookNumber(9)
        .description(hdr2).build();

    SELibManager.bookBuilder()
        .bookNumber(10)
        .description(hdr3).build();
    Optional<Book> hdr3Book = SELibManager.bookBuilder()
        .bookNumber(11)
        .description(hdr3).build();

    ArrayList<String> comments = new ArrayList<>();
    HashSet<Book> rentedBooks = new HashSet<>();
    comments.add("Wimi");
    comments.add("IoP");
    comments.add("MaCoCo");
    rentedBooks.add(hdr1Book.get());
    rentedBooks.add(hdr2Book.get());
    rentedBooks.add(hdr3Book.get());
    SELibManager.personBuilder()
        .name("Malte Heithoff")
        .email("Heithoff@SE-RWTH.de")
        .password("Pass")
        .isManager(true)
        .comments(comments)
        .test_that_merge_is_working("Merge funktioniert")
        .rentedBooks(rentedBooks)
        .favorite(hdr1Book.get())
        .build();
  }
}
