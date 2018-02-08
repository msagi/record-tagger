## Records API
* [Create record](#markdown-header-create-record)
* [Read record by ID](#markdown-header-read-record-by-id)
* [Read all records filtered by tags and paginated](#markdown-header-read-all-records-filtered-by-tags-and-paginated)
* [Update record by ID](#markdown-header-update-record-by-id)
* [Delete record by ID](#markdown-header-delete-record-by-id)

## Tags API

* [Create tag](#markdown-header-create-tag)
* [Read tag by ID](#markdown-header-read-tag-by-id)
* [Read all tags with some tags selected](#markdown-header-read-all-tags-with-some-tags-selected)
* [Update tag by ID](#markdown-header-update-tag-by-id)
* [Delete tag by ID](#markdown-header-delete-tag-by-id)

## Tagging API

* [Tag a record](#markdown-header-tag-a-record)
* [Untag a record](#markdown-header-untag-a-record)

# Create record
----
  _Create new record in the repository. The call returns the tag object in HAL+JSON format._

* **URL:**  `/records`

* **Method:** `POST`

* **Data Params**

   **Required:**

   `record=[alphanumeric, UTF-8]`

* **Success Response:**

  * **Code:** 201 Created
  
    **Content:** 
    
```JSON
{
  "_links": {
    "self": {
      "href": "/records/1"
    }
  },
  "id": 1,
  "record": "This is the record value",
  "tags": [
    {
      "id": 1,
      "tag": "new"
    },
    {
      "id": 2,
      "tag": "lang:en"
    }
  ]
}
```

* **Error Response:**

  _If record parameter is missing in the request body:_

  * **Code:** 400 Bad Request
  
    **Content:** _No content_

  OR

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

```javascript
    createRecord(recordValue, callback, errorCallback) {
        $.ajax({
            url: '/records',
            type: 'POST',
            contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
            data: { 'record': recordValue },
            success: function(data, status) {
                callback(data, status);
            },
            error: function(data, status, error) {
                errorCallback(status, error)
            }
        });
    }
```

# Read record by ID
----
  _Read a record from the repository by ID. The call returns the tag object in HAL+JSON format._

* **URL:** `/records/{id}`

* **Method:** `GET`
  
*  **Path Params:**

   **Required:**
 
   `id as [integer]`

* **Success Response:**

  _If the record found:_

  * **Code:** 200 OK
  
    **Content:** 

```JSON
    {
      "_links": {
        "self": {
          "href": "/records/1"
        }
      },
      "id": 1,
      "record": "This is the record value",
      "tags": [
        {
          "id": 1,
          "tag": "new"
        },
        {
          "id": 2,
          "tag": "lang:en"
        }
      ]
    }
```

  OR

  _If the repository contains no record for the given ID:_

  * **Code:** 404 Not Found
  
    **Content:** _No content_
 
* **Error Response:**
    
  _If the required path segment `{id}` is missing:_

  * **Code:** 400 Bad Request
  
    **Content:** _No content_

  OR

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

```javascript
    readRecord(recordId, callback, errorCallback) {
        $.ajax({
            url: '/records' + recordId,
            type: 'GET',
            success: function(data, status) {
                callback(data, status);
            },
            error: function(data, status, error) {
                errorCallback(status, error)
            }
        });
    }
```

# Read all records filtered by tags and paginated
----
  _Read all records from the repository with some tags selected. The record list is paginated. The call returns the tag object in HAL+JSON format._

* **URL:** `/records`

* **Method:** `GET`
  
*  **Query Params:**

   **Optional:**
 
   `tags=[string, comma separated tag values]`

   `limit=[integer, number of records to return [1..30]]`

   `offset=[integer, the offset in the records list]`

* **Success Response:**

  _If the query was successful:_

  * **Code:** 200 OK
  
    **Content:** 

```JSON
{
  "_links": {
    "next": {
      "href": "/records?tags=&limit=2&offset=2"
    },
    "last": {
      "href": "/records?tags=&limit=2&offset=998"
    },
    "self": {
      "href": "/records?tags=&limit=2&offset=0"
    },
    "tags": {
      "href": "/tags?selected="
    }
  },
  "_embedded": {
    "records": [
      {
        "_links": {
          "self": {
            "href": "/records/1"
          }
        },
        "id": 1,
        "record": "This is the record value",
        "tags": [
          {
            "id": 1,
            "tag": "new"
          },
          {
            "id": 2,
            "tag": "lang:en"
          }
        ]
      },
      {
        "_links": {
          "self": {
            "href": "/records/2"
          }
        },
        "id": 2,
        "record": "This is a second record value",
        "tags": [
          {
            "id": 1,
            "tag": "new"
          },
          {
            "id": 2,
            "tag": "lang:en"
          },
          {
            "id": 3,
            "tag": "type:simple"
          }
        ]
      }
    ]
  },
  "total": 2
}
```
 
* **Error Response:**

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

  _Hint: use the HAL links (_links.first, _links.last, _links.prev, _links.next) for paginated navigation. Read the matching tags using the _links.tags HAL
   link._

```javascript
    readRecords(link = '/records', callback, errorCallback) {
        $.ajax({
            url: link,
            type: 'GET',
            success: function(data, status) {
                callback(data, status);
            },
            error: function(data, status, error) {
                errorCallback(status, error)
            }
        });
    }
```

# Update record by ID
----
  _Update existing record in the repository by ID. The call returns the tag object in HAL+JSON format._

* **URL:** `/records/{id}`

* **Method:** `PUT`
  
*  **Path Params:**

   **Required:**
 
   `id as [integer]`

* **Data Params**

   **Required:**

   `record=[alphanumeric, UTF-8]`
   
* **Success Response:**

  _If the record found:_

  * **Code:** 201 Created
  
    **Content:** 

```JSON
    {
      "_links": {
        "self": {
          "href": "/records/1"
        }
      },
      "id": 1,
      "record": "This is the NEW record value",
      "tags": [
        {
          "id": 1,
          "tag": "new"
        },
        {
          "id": 2,
          "tag": "lang:en"
        }
      ]
    }
```

  OR

  _If the repository contains no record for the given ID:_

  * **Code:** 404 Not Found
  
    **Content:** _No content_
 
* **Error Response:**
    
  _If the required path segment `{id}` is missing or not a number or the required data parameter `{record}` is missing:_

  * **Code:** 400 Bad Request
  
    **Content:** _No content_

  OR

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

```javascript
    updateRecord(recordId, newRecordValue, callback, errorCallback) {
        $.ajax({
            url: '/records/' + recordId,
            type: 'PUT',
            contentType: 'application/x-www-form-urlencoded',
            data: { 'record': newRecordValue },
            success: function(data, status){
                callback(data);
            },
            error: function(data, status, error) {
                errorCallback(status, error)
            }
        });
    }
```

# Delete record by ID
----
  _Delete existing reocrd in the repository by ID._

* **URL:** `/records/{id}`

* **Method:** `DELETE`
  
*  **Path Params:**

   **Required:**
 
   `id as [integer]`

* **Success Response:**

  _If the record found:_

  * **Code:** 204 No Content
  
    **Content:** _No content_

  OR

  _If the repository contains no record for the given ID:_

  * **Code:** 404 Not Found
  
    **Content:** _No content_
 
* **Error Response:**
    
  _If the required path segment `{id}` is missing or not a number:_

  * **Code:** 400 Bad Request
  
    **Content:** _No content_

  OR

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

```javascript
    deleteRecord(recordId, callback, errorCallback) {
        $.ajax({
            url: '/records/' + recordId,
            type: 'DELETE',
            success: function(data, status) {
                callback(data)
            },
            error: function(data, status, error) {
                errorCallback(status, error)
            }
        });
    }
```

# Create tag
----
  _Create new tag in the repository. The call returns the tag object in HAL+JSON format._

* **URL:**  `/tags`

* **Method:** `POST`

* **Data Params**

   **Required:**

   `tag=[alphanumeric, UTF-8]`

* **Success Response:**

  * **Code:** 201 Created
  
    **Content:** 
    
```JSON
  {
    "_links": {
      "self": {
        "href": "/tags/1"
      }
    },
    "id": 1,
    "tag": "tagValue"
  }
```

* **Error Response:**

  _If tag parameter is missing in the request body:_

  * **Code:** 400 Bad Request
  
    **Content:** _No content_

  OR

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

```javascript
    createTag(tagValue, callback, errorCallback) {
        $.ajax({
            url: '/tags',
            type: 'POST',
            contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
            data: { 'tag': tagValue },
            success: function(data, status) {
                callback(data)
            },
            error: function(data, status, error) {
                errorCallback(status, error)
            }
        });
    }
```

# Read tag by ID
----
  _Read a tag from the repository by ID. The call returns the tag object in HAL+JSON format._

* **URL:** `/tags/{id}`

* **Method:** `GET`
  
*  **Path Params:**

   **Required:**
 
   `id as [integer]`

* **Success Response:**

  _If the tag found:_

  * **Code:** 200 OK
  
    **Content:** 

```JSON
  {
    "_links": {
      "self": {
        "href": "/tags/1"
      }
    },
    "id": 1,
    "tag": "tagValue"
  }
```

  OR

  _If the repository contains no tag for the given ID:_

  * **Code:** 404 Not Found
  
    **Content:** _No content_
 
* **Error Response:**
    
  _If the required path segment `{id}` is missing:_

  * **Code:** 400 Bad Request
  
    **Content:** _No content_

  OR

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

```javascript
    readTag(tagId, callback, errorCallback) {
        $.ajax({
            url: '/tags/' + tagId,
            type: 'GET',
            success: function(data, status) {
                callback(data)
            },
            error: function(data, status, error) {
                errorCallback(status, error)
            }
        });
    }
```

# Read all tags with some tags selected
----
  _Read all tags from the repository with some tags selected. The call returns the tag object in HAL+JSON format._

* **URL:** `/tags`

* **Method:** `GET`
  
*  **Query Params:**

   **Optional:**
 
   `selected as [string, comma separated tag values]`

* **Success Response:**

  _If the tag found:_

  * **Code:** 200 OK
  
    **Content:** 

```JSON
{
  "selected": [
    {
      "id": 1,
      "tag": "lang:en"
    },
    {
      "id": 2,
      "tag": "timer:5"
    }
  ],
  "tags": [
    {
      "_links": {
        "records": {
          "href": "/records?tags=timer:5"
        },
        "self": {
          "href": "/tags/1"
        }
      },
      "id": 1,
      "tag": "lang:en"
      },
    {
      "_links": {
        "records": {
          "href": "/records?tags=lang:en"
        },
        "self": {
          "href": "/tags/2"
        }
      },
      "id": 2,
      "tag": "timer:5"
    },
    {
      "_links": {
        "records": {
          "href": "/records?tags=lang:en,timer:5,lang:de"
        },
        "self": {
          "href": "/tags/3"
        }
      },
      "id": 3,
      "tag": "lang:de"
    }
  ],
  "total": 3
}
```
 
* **Error Response:**

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

```javascript
    readTags(link = '/tags', callback, errorCallback) {
        $.ajax({
            url: link,
            type: 'GET',
            success: function(data, status) {
                callback(data, status);
            },
            error: function(data, status, error) {
                errorCallback(status, error)
            }
        });
    }
```

# Update tag by ID
----
  _Update existing tag in the repository by ID. The call returns the tag object in HAL+JSON format._

* **URL:** `/tags/{id}`

* **Method:** `PUT`
  
*  **Path Params:**

   **Required:**
 
   `id as [integer]`

* **Data Params**

   **Required:**

   `tag=[alphanumeric, UTF-8]`
   
* **Success Response:**

  _If the tag found:_

  * **Code:** 201 Created
  
    **Content:** 

```JSON
  {
    "_links": {
      "self": {
        "href": "/tags/1"
      }
    },
    "id": 1,
    "tag": "newTagValue"
  }
```

  OR

  _If the repository contains no tag for the given ID:_

  * **Code:** 404 Not Found
  
    **Content:** _No content_
 
* **Error Response:**
    
  _If the required path segment `{id}` is missing or not a number or the required data parameter `{tag}` is missing:_

  * **Code:** 400 Bad Request
  
    **Content:** _No content_

  OR

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

```javascript
    updateTag(tagId, newTagValue, callback, errorCallback) {
        $.ajax({
            url: '/tags/' + tagId,
            type: 'PUT',
            contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
            data: { 'tag': newTagValue },
            success: function(data, status) {
                 callback(data);
            },
            error: function(data, status, error) {
                 errorCallback(status, error)
            }
        });
    }
```

# Delete tag by ID
----
  _Delete existing tag in the repository by ID._

* **URL:** `/tags/{id}`

* **Method:** `DELETE`
  
*  **Path Params:**

   **Required:**
 
   `id as [integer]`

* **Success Response:**

  _If the tag found:_

  * **Code:** 204 No Content
  
    **Content:** _No content_

  OR

  _If the repository contains no tag for the given ID:_

  * **Code:** 404 Not Found
  
    **Content:** _No content_
 
* **Error Response:**
    
  _If the required path segment `{id}` is missing or not a number:_

  * **Code:** 400 Bad Request
  
    **Content:** _No content_

  OR

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

```javascript
    deleteTag(tagId, callback, errorCallback) {
        $.ajax({
            url: '/tags/' + tagId,
            type: 'DELETE',
            success: function(data, status) {
                 callback(data)
            },
            error: function(data, status, error) {
                errorCallback(status, error)
            }
        });
    }
```

# Tag a record
----
  _Tag an existing record with an existing tag in the repository. The call returns the tag object in HAL+JSON format._

* **URL:**  `/records/{recordId}/tags/{tagId}`

* **Method:** `POST`

* **Path Params**

   **Required:**

   `recordId as [integer, the ID of the record]`

   `tagId as [integer, the ID of the tag]`

* **Success Response:**

  * **Code:** 201 Created
  
    **Content:** The updated record content (e.g. the new tag (id:3, tag:"type:simple") has been added to the tags list of the record)

```JSON
    {
      "_links": {
        "self": {
          "href": "/records/1"
        }
      },
      "id": 1,
      "record": "This is the record value",
      "tags": [
        {
          "id": 1,
          "tag": "new"
        },
        {
          "id": 2,
          "tag": "lang:en"
        },
        {
          "id": 3,
          "tag": "type:simple"
        }
      ]
    }
```

* **Error Response:**

  _If recordId or tagId parameter is missing in the request path:_

  * **Code:** 400 Bad Request
  
    **Content:** _No content_

  OR

  _If no record or tag found by the recordId and tagId parameter:_

  * **Code:** 404 Not Found
  
    **Content:** _No content_

  OR

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

```javascript
    tag(recordId, tagId, callback, errorCallback) {
        $.ajax({
            url: '/records/' + recordId + '/tags/' + tagId,
            type: 'POST',
            success: function(data, status){
                callback(data);
            },
            error: function(data, status, error) {
                errorCallback(status, error)
            }
        });
    }
```

# Untag a record
----
  _Untag an existing record with an existing tag in the repository. The call returns the tag object in HAL+JSON format._

* **URL:**  `/records/{recordId}/tags/{tagId}`

* **Method:** `DELETE`

* **Path Params**

   **Required:**

   `recordId as [integer, the ID of the record]`

   `tagId as [integer, the ID of the tag]`

* **Success Response:**

  * **Code:** 200 OK
  
    **Content:** The updated record content (e.g. the tag (id:3, tag:"type:simple") has been removed from the tags list of the record)

```JSON
    {
      "_links": {
        "self": {
          "href": "/records/1"
        }
      },
      "id": 1,
      "record": "This is the record value",
      "tags": [
        {
          "id": 1,
          "tag": "new"
        },
        {
          "id": 2,
          "tag": "lang:en"
        }
      ]
    }
```

* **Error Response:**

  _If recordId or tagId parameter is missing in the request path:_

  * **Code:** 400 Bad Request
  
    **Content:** _No content_

  OR

  _If no record or tag found by the recordId and tagId parameter:_

  * **Code:** 404 Not Found
  
    **Content:** _No content_

  OR

  _If an internal error happened during serving the request:_

  * **Code:** 500 Internal Server Error
  
    **Content:** _No content_

* **Sample Call:**

```javascript
    untag(recordId, tagId, callback, errorCallbac) {
        $.ajax({
            url: '/records/' + recordId + '/tags/' + tagId,
            type: 'DELETE',
            success: function(data, status){
                callback(data);
            },
            error: function(data, status, error) {
                errorCallback(status, error)
            }
        });
    }
```
