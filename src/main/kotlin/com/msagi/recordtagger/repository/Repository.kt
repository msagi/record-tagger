package com.msagi.recordtagger.repository

interface Repository {

    /**
     * Initialize the repository if it has not been initialized so that it is ready to start working.
     */
    fun initialize()

    /**
     * Reset all the internal state in the repository.
     */
    fun reset()

    // RECORDS API

    /**
     * Add new record to the repository. This method has no effect if the record value has already been added.
     * @param recordValue The value of the record which is unique in the repository.
     */
    fun addRecord(recordValue: String): Record

    /**
     * Get a record by its ID.
     * @param recordId The record ID.
     * @return The record of the given ID if found in the repository, null otherwise.
     */
    fun getRecord(recordId: Int): Record?

    /**
     * Set record value by its ID.
     * @param recordId The record ID.
     * @param newRecordValue The new value for the record.
     * @return The updated record if the given ID found in the repository.
     * @throws IllegalArgumentException If the record is not found in the repository.
     */
    fun setRecord(recordId: Int, newRecordValue: String): Record

    /**
     * Remove record from the repository with all of its references to tags. This method has no effect if the record is not found.
     * @param recordId The record ID.
     */
    fun removeRecord(recordId: Int)

    /**
     * Get the number of records in the repository filtered by the given list of tags in logical AND relation.
     * @param tags The list of tags to filter the records with.
     * @return The number of records.
     * @throws IllegalArgumentException If tags list contains any tags which do not exist in repository.
     */
    fun getNumberOfRecords(tags: List<Tag> = listOf()): Int

    /**
     * Get one page of records in ascending order by their IDs (the order they were created in the repository).
     * @param tags The list of tags to filter the records with. The default value is empty list (means no filter).
     * @param limit The limit of the number of records to get. The default value is 30.
     * @param offset The offset of the records to get. The default value is 0.
     * @return The list of records.
     * @throws IllegalArgumentException If tags list contains any tags which do not exist in repository.
     */
    fun getRecords(tags: List<Tag> = listOf(), limit: Int = 30, offset: Int = 0): List<Record>

    // TAGS API

    /**
     * Add new tag to the repository. This method has no effect if the tag has already been added.
     * @param tagValue The value of the tag which is unique in the repository.
     * @return The new tag.
     */
    fun addTag(tagValue: String): Tag

    /**
     * Get tag by its ID.
     * @param tagId The tag ID.
     * @return The tag if found, null otherwise.
     */
    fun getTag(tagId: Int): Tag?

    /**
     * Set tag by its ID.
     * @param tagId The tag ID.
     * @return The tag if found and updated.
     * @throws IllegalArgumentException If the tag is not found in the repository.
     */
    fun setTag(tagId: Int, tagValue: String): Tag

    /**
     * Remove tag from the repository with all of its references to records. This method has no effect if the tag is not found.
     * @param tagId The ID of the tag.
     */
    fun removeTag(tagId: Int)

    /**
     * Get all the tags in the repository in ascending alphabetical order by tag value.
     */
    fun getAllTags(): List<Tag>

    /**
     * Get tags by their tag values in ascending alphabetical order by tag value.
     * @param tagValues The list of tag values.
     * @return The list of tags which have matching tag values.
     */
    fun getTagsByValues(tagValues: List<String>): List<Tag>

    // TAGGING API

    /**
     * Add tag to the record. This method has no effect if the record has already been tagged with the tag.
     * @param recordId The record ID.
     * @param tagId The tag ID.
     * @return The updated record.
     * @throws IllegalArgumentException If the record or tag is not found in the repository.
     */
    fun tag(recordId: Int, tagId: Int): Record

    /**
     * Remove the tag from the record (if it has been added). This method has no effect if the record is not
     * tagged with the tag in the repository.
     * @param recordId The record ID.
     * @param tagId The tag ID.
     * @return The updated record.
     * @throws IllegalArgumentException If the record or tag is not found in the repository.
     */
    fun untag(recordId: Int, tagId: Int): Record

}