package com.library.presolid;

/**
 * [ISP VIOLATION - ASSIGNED TO MEMBER 4]
 * Concrete Book entity forced to implement methods like streamAudioFile()
 * from ILibraryOperations.
 */
public class Book implements ILibraryOperations {
    private String id;
    private String isbn;
    private String title;
    private String author;
    private String category;
    private boolean isIssued;
    private String rackLocation;

    public Book(String id, String isbn, String title, String author, String category, String rackLocation) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.rackLocation = rackLocation;
        this.isIssued = false;
    }

    public String getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public boolean isIssued() { return isIssued; }
    public void setIssued(boolean issued) { this.isIssued = issued; }
    public String getRackLocation() { return rackLocation; }

    @Override
    public void borrowItem(String memberId) {
        if (isIssued) {
            System.out.println("Book '" + title + "' is already issued.");
            return;
        }
        this.isIssued = true;
        System.out.println("Book '" + title + "' borrowed by member: " + memberId);
    }

    @Override
    public void returnItem() {
        this.isIssued = false;
        System.out.println("Book '" + title + "' returned successfully.");
    }

    @Override
    public void renewItem() {
        System.out.println("Book '" + title + "' renewed for an additional period.");
    }

    @Override
    public void reserveItem(String memberId) {
        System.out.println("Book '" + title + "' reserved for member: " + memberId);
    }

    // --- FORCED ISP METHODS (Unused or Dummy implementations) ---
    @Override
    public void printBarcodeLabel() {
        System.out.println("[BARCODE] Printing barcode label for physical book: " + isbn);
    }

    @Override
    public void streamAudioFile() {
        // [ISP Flaw] Physical printed books cannot stream audio!
        System.out.println("[ERROR - ISP VIOLATION] Physical book cannot stream audio! Method forced by ILibraryOperations.");
    }

    @Override
    public void exportMetadataToPdf() {
        System.out.println("[PDF] Exporting metadata for book: " + title);
    }
}
