package com.library.presolid;

/**
 * [LSP VIOLATION - ASSIGNED TO MEMBER 3]
 * ReferenceBook inherits from Book, but reference materials cannot be checked out.
 * Throwing UnsupportedOperationException violates behavioral subtyping:
 * Code expecting a Book cannot safely substitute a ReferenceBook.
 */
public class ReferenceBook extends Book {
    private String readingRoomSection;

    public ReferenceBook(String id, String isbn, String title, String author, String category, String readingRoomSection) {
        super(id, isbn, title, author, category, readingRoomSection);
        this.readingRoomSection = readingRoomSection;
    }

    public String getReadingRoomSection() {
        return readingRoomSection;
    }

    @Override
    public void borrowItem(String memberId) {
        // [LSP Flaw] Violates Liskov Substitution Principle by throwing runtime exception
        throw new UnsupportedOperationException("LSP VIOLATION: Reference book '" + getTitle() + "' cannot be borrowed! Reading room use only.");
    }

    @Override
    public void renewItem() {
        // [LSP Flaw] Cannot renew an item that cannot be borrowed
        throw new UnsupportedOperationException("LSP VIOLATION: Cannot renew reference book '" + getTitle() + "'.");
    }
}
