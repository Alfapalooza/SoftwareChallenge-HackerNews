# HackerNews Challenge

### How to run
From project directory:
```
sbt run
```

By default the configuration port is set to *8080*, to get the challenge listing, in you browser, navigate to:
```
localhost:8080/api/hacker-news/top-stories
```

The above call will produce a response that adheres to this schema:
```
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "definitions": {
    "user": {
      "properties": {
        "id": {
          "type": "string"
        },
        "storyCommentsCount": {
          "type": "number"
        },
        "totalCommentsCount": {
          "type": "number"
        }
      },
      "required": [
        "id",
        "storyCommentsCount",
        "totalCommentsCount"
      ],
      "type": "object"
    },
    "comment": {
      "properties": {
        "id": {
          "type": "number"
        },
        "user": {
          "allOf": [
            {
              "$ref": "#/definitions/user"
            }
          ]
        }
      },
      "required": [
        "id",
        "user"
      ],
      "type": "object"
    },
    "story": {
      "properties": {
        "title": {
          "type": "string"
        },
        "commentIds": {
          "items": {
            "type": "number"
          },
          "type": "array"
        }
      },
      "required": [
        "title",
        "commentIds"
      ],
      "type": "object"
    },
    "storyWithComments": {
      "properties": {
        "story": {
          "allOf": [
            {
              "$ref": "#/definitions/story"
            }
          ]
        },
        "comments": {
          "items": {
            "allOf": [
              {
                "$ref": "#/definitions/comment"
              }
            ]
          },
          "type": "array"
        }
      },
      "required": [
        "story",
        "comments"
      ],
      "type": "object"
    }
  },
  "properties": {
    "stories": {
      "items": {
        "allOf": [
          {
            "$ref": "#/definitions/storyWithComments"
          }
        ]
      },
      "type": "array"
    }
  },
  "required": [
    "stories"
  ],
  "type": "object"
}
```
