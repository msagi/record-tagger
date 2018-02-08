/*
 * Copyright 2018 Miklos Sagi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class RecordTagger {

    // records API

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

    // tags API

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

    // tagging API

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
}
