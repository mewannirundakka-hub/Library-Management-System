package com.library.presolid;

/**
 * [ISP VIOLATION - ASSIGNED TO MEMBER 4]
 * Fat, bloated interface forcing every library item to implement methods
 * that may not make sense for physical books, digital media, or reference materials.
 */
public interface ILibraryOperations {
    void borrowItem(String memberId);
    void returnItem();
    void renewItem();
    void reserveItem(String memberId);
    
    // Unrelated capabilities forced onto implementing classes
    void printBarcodeLabel();
    void streamAudioFile();
    void exportMetadataToPdf();
}
