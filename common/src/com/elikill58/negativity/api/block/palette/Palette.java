package com.elikill58.negativity.api.block.palette;

public interface Palette {

    /**
     * Returns the packet section index of the given coordinates.
     *
     * @param x x
     * @param y y
     * @param z z
     * @return packed section index of the given coordinates
     */
    int index(final int x, final int y, final int z);

    /**
     * Returns the value of the given chunk coordinate.
     *
     * @param sectionCoordinate section index within the section
     * @return section state of the given index
     */
    int idAt(int sectionCoordinate);

    /**
     * Returns the value of the section coordinate.
     *
     * @param sectionX section x
     * @param sectionY section y
     * @param sectionZ section z
     * @return id of the given section coordinate
     */
    default int idAt(final int sectionX, final int sectionY, final int sectionZ) {
        return idAt(index(sectionX, sectionY, sectionZ));
    }

    /**
     * Set a value in the chunk section.
     * This method does not update non-air blocks count.
     *
     * @param sectionCoordinate section index within the section
     * @param id                id value
     */
    void setIdAt(int sectionCoordinate, int id);

    /**
     * Set a value in the chunk section.
     * This method does not update non-air blocks count.
     *
     * @param sectionX section x
     * @param sectionY section y
     * @param sectionZ section z
     * @param id       id value
     */
    default void setIdAt(final int sectionX, final int sectionY, final int sectionZ, final int id) {
        setIdAt(index(sectionX, sectionY, sectionZ), id);
    }

    /**
     * Returns the id assigned to the given palette index.
     *
     * @param index palette index
     * @return id assigned to the given palette index
     */
    int idByIndex(int index);

    /**
     * Assigns an id assigned to the given palette index.
     *
     * @param index palette index
     * @param id    id value
     */
    void setIdByIndex(int index, int id);

    /**
     * Returns the palette index of the given section index.
     *
     * @param packedCoordinate section index
     * @return palette index of the given section index
     */
    int paletteIndexAt(int packedCoordinate);

    /**
     * Sets the index of the given section coordinate.
     *
     * @param sectionCoordinate section index
     * @param index             palette index
     */
    void setPaletteIndexAt(int sectionCoordinate, int index);

    /**
     * Adds a new id to the palette.
     *
     * @param id id value
     */
    void addId(int id);

    /**
     * Replaces an id in the palette.
     *
     * @param oldId old id
     * @param newId new id
     */
    void replaceId(int oldId, int newId);

    /**
     * Returns the size of the palette.
     *
     * @return palette size
     */
    int size();

    /**
     * Clears the palette.
     */
    void clear();
    
    int[] getValues();
   
}
