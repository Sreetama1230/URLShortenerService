# URL Shortener Service

Implemented a URL Shortener service using **PostgreSQL** for persistent storage and **Redis** for caching and maintaining a unique counter.

## How It Works

* A unique counter is maintained in Redis.
* The current counter value is converted into a **Base62-encoded string** to generate a short URL code.
* The generated short code is limited to a maximum of **6 Base62 characters**.
* Each shortened URL is valid for **1 hour**.
* When a user accesses the short URL, the service returns a **302 Temporary Redirect** to the original URL.
* PostgreSQL is used to persist the URL details, while Redis is used for the counter.

## APIs

The service provides two main APIs:

### 1. Create Short URL

This API creates a shortened URL for the provided original URL.

**Endpoint:**

```http
POST /v1/url/shorturl
```

**Example Response:**

```json
{
    "longURL": "https://www.youtube.com/",
    "shortURL": "http://localhost:8080/v1/url/myyt",
    "expires_at": "2026-09-21T04:09:55.334067746"
}
```

The response contains:

* `longURL` - The original URL.
* `shortURL` - The generated shortened URL.
* `expires_at` - The expiry time of the shortened URL.

### 2. Redirect to Original URL

The generated short URL can be directly pasted into a browser.

For example:

```text
http://localhost:8080/v1/url/myyt
```

The service looks up the corresponding original URL and returns a **302 Temporary Redirect**.

The browser then automatically redirects the user to:

```text
https://www.youtube.com/
```

Each shortened URL remains valid for **1 hour**. After expiration, the short URL can no longer be used.

## Base62 Encoding

To generate compact short URL codes, the service converts the Redis counter value into a Base62-encoded string.

The Base62 character set contains:

```text
0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz
```

Using 62 characters, the system can generate:

```text
62^6 = 56,800,235,584
```

possible combinations using a maximum of 6 characters.

The service ensures that the counter remains within the supported range:

```text
0 <= counter <= 62^6 - 1
```

## Architecture

```text
                    Client
                      |
                      |
                      v
              +---------------+
              | URL Controller |
              +---------------+
                      |
                      v
              +---------------+
              |  URL Service   |
              +---------------+
                 /          \
                /            \
               v              v
        +------------+    +---------+
        | PostgreSQL |    |  Redis  |
        +------------+    +---------+
                              |
                              v
                         Counter
                              |
                              v
                           Base62
                              |
                              v
                         Short Code
```

## URL Expiration

Each shortened URL is assigned an expiry time of **1 hour** from its creation time.

During a redirect request, the service checks whether the URL has expired.

```text
Create URL
    |
    v
Current Time
    |
    +---- 1 Hour ----+
                     |
                     v
                 Expiration
```

If the URL has expired, the service does not perform the redirect.

## Tech Stack

* **Java**
* **Spring Boot**
* **PostgreSQL**
* **Redis**
* **JPA / Hibernate**
* **REST API**
* **Base62 Encoding**

## Example Flow

```text
1. Client sends the original URL
          |
          v
2. Service generates a unique counter using Redis
          |
          v
3. Counter is converted to Base62
          |
          v
4. Short code is generated
          |
          v
5. URL details are stored in PostgreSQL
          |
          v
6. Short URL is returned to the client
          |
          v
7. User opens the short URL
          |
          v
8. Service validates the URL and its expiry
          |
          v
9. Service returns HTTP 302 Redirect
          |
          v
10. Browser opens the original URL
```

## Screenshot

![URL Shortener Service](https://github.com/user-attachments/assets/c5e9a98a-20d7-4d03-a1d5-56d7595a73b7)
