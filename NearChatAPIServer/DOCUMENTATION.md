# NearChat JSON API Server Documentation
The NearChat JSON API server is a flexible, modular and secure data storage, management and exchange system that encompasses secure access control to NearChat informational assets, storage and retrieval of NearChat account asset and lifecycle information, in simple, machine-parsable and language-agnostic JSON (Javascript Object notation, see [here](http://www.json.org/fatfree.html) for a brief overview) format.

# General command specification
The NearChat JSON server can be accessed from the URL ``http://SERVER_ADDRESS/NearChat/api.json`` for authentication and data exchange. Any request to the server consists of the GET/POST parameters ``mode`` to specify the action to be performed on the server and ``token`` to specify the access token required for authorization to perform restricted actions (everything except for login), as well as additional parameters specific to the action specified.

# Quick Start Command Examples
- To log in to an account and retrieve an access token, the command URL is ``http://SERVER_ADDRESS/NearChat/api.json?mode=login&username={username}&password={password}``, where the ``mode`` parameter is specified as ``login`` to specify that the action is account login, and the ``username`` and ``password`` parameters specify the respective username and password of the account from which to retrieve the access token.

- To get the information of the account of a corresponding login token, the command URL is ``http://SERVER_ADDRESS/NearChat/api.json?mode=get_current_account_info&token={token}``, where the ``mode`` parameter is specified as ``get_current_account_info`` to specify that the action is to retrieve the information of the current account, and the ``token`` parameter specifies the unique access token used to perform actions with the account specified.

- For more information on specific commands, please see the documentation below.

# Authentication
- To securely manage access to NearChat information assets, a token-based authentication system is implemented for access control on the NearChat JSON API server. Upon successful authentication with the login credentials of the account, a secure, randomly-generated alphanumeric UUID (universal unique identifier, see [here](https://www.ietf.org/rfc/rfc4122.txt) for a brief overview) token is used for all future interactions that interaction

- All authentication commands for account access/creation return JSON information in the following format, that describes whether the authentication attempt was successful, an access token if the authentication attempt was successful, and an optional reason description that may display miscellaneous descriptions such as error and diagnostic codes.

```
{
    "token":"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "status":"{success | failure}",
    "reason":"{sample reason message}"
}
```
- In the previous JSON response shown, the ``token`` field contains the alphanumeric string that will be used as the access token for future restricted actions (if login successful), the ``status`` field contains a string denoting whether the authentication action was successful (``success`` if successful, ``failure`` if action failed), and the optional ``reason`` field contains diagnostic/error codes that denote the reason for an error. The ``token`` or ``reason`` fields may or may not be included in the authentication response for various reasons, such as an unsuccessful authentication attempt or if authentication succeeds as normal.

- It is recommend to parse the authentication response by reading the ``status`` token first to check if the authentication action was successful before parsing other fields that may or may not be included in the response based on the result of the action. such as ``token`` or ``reason``.

- *NOTE*: At no point is it possible to retrieve the actual stored password of any specified account. Details of passwords are stored in hashed form on the server database for security purposes for comparison.

# Error codes
Here is a list of error codes (Denoted with the ``reason`` parameter in server JSON responses) that the server is programmed to return if a request fails.

```
    ERR_GENERIC,
    ERR_ACCOUNT_EMAIL_EXISTS,
    ERR_ACCOUNT_USERNAME_EXISTS,
    ERR_ACCOUNT_USERNAME_AND_EMAIL_EXISTS,
    ERR_CREDENTIALS_MISSING,
    ERR_ACCESS_TOKEN_EMPTY,
    ERR_ACCESS_TOKEN_INVALID,
    ERR_ACCESS_PERMISSION_DENIED,
    ERR_MODE_NOT_RECOGNIZED,
    ERR_MODE_EMPTY,
    ERR_LOGIN_UNSUCCESSFUL,
    ERR_ACCOUNT_NONEXISTANT,
    ERR_PARAMETERS_MISSING_OR_INVALID,
    ERR_USER_UNDERAGE
```

# User JSON Specification

Instances of users on the NearChat server are retrieved using the following JSON specification. 

Example:
```
{
    "id":123456789,
    "username":"MyUsername",
    "age":18,
    "gender":"Male",
    "relationship_status":"Single",
    "bio":"Sample bio text",
    "interests":["cars","magic the gathering","rc planes","anime","hiking"],
    "telegram":"@MyTelegramUsername",
    "visible":true
}
```
- ``id`` is a 64-bit integer denoting the internal server id of the account.
- ``username`` is the username of the account.
- ``age`` is a 64-bit integer denoting the age of the account user.
- ``email`` is the e-mail address of the account.
- ``relationship_status`` is a string w/ the relationship status of the account.
- ``bio`` is a string w/ the biography text of the account.
- ``interests`` is a JSON array of strings listing the interests of the user.
- ``telegram`` is the user's username on the Telegram messaging app.
- ``visible`` is whether the user is visible to other users.

# Creating an account
*Description*: Creates an account with the following parameters if none exists with the same details, and returns an access token for the account.

**Required GET/POST parameters**
- ``mode`` should be set to ``create_account``
- ``username`` should be set to the username of the account to be created.
- ``email`` should be set to the email address of the user of the account.
- ``password`` should be set to the password of the account, in UTF-8 plaintext.

**Example command**

```
http://SERVER_ADDRESS/NearChat/api.json?mode=create_account&username={username}&email={email}&password={password}
```

**Example response**

```
{
    "token":"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "status":"{success | failure}",
    "reason":"{sample reason message}"
}
```
- ``token`` is the newly-generated access token of the account created.
- ``status`` indicates whether account creation was successful.
- ``reason`` indicates the reason for account creation failure.
- *Notes*: ``token`` and ``reason`` can be NULL/nonexistent.

# Logging into an existing account
*Description*: Returns an access token for the account with the given parameters below.

**Required GET/POST parameters**
- ``mode`` should be set to ``login``
- ``username`` should be set to the username of the account to log into.
- ``password`` should be set to the password of the account, in UTF-8 plaintext.

**Example command**

```
http://SERVER_ADDRESS/NearChat/api.json?mode=login&username={username}&password={password}
```

**Example response**

```
{
    "token":"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
    "status":"{success | failure}",
    "reason":"{sample reason message}"
}
```
- ``token`` is the newly-generated access token of the account logged into.
- ``status`` indicates whether account login was successful.
- ``reason`` indicates the reason for login failure.
- *Notes*: ``token`` and ``reason`` can be NULL/nonexistent.

# Updating the information of an account
*Description*: Updates specified fields of the account, such as username, password, email, gender, etc.

**Required GET/POST parameters**
- ``mode`` should be set to ``update_info``
- ``token`` should be set to the access token of the logged-in account.
- ``fields`` should be the new values of the user instance corresponding to the token, in JSON object format.

**Example command**

```
http://SERVER_ADDRESS/NearChat/api.json?mode=update_info&token={token}&fields={fields}
```

**Example response**

```
{
    "status":"{success | failure}",
    "reason":"{sample reason message}"
}
```

- ``status`` indicates whether the information update was successful.
- ``reason`` indicates the reason for failure if there is any.
- *Notes*: ``reason`` can be NULL/nonexistent.

# Updating the geolocation of an account
*Description*: Updates the longitude and latitude of the account, which are kept private from other users.

**Required GET/POST parameters**
- ``mode`` should be set to ``update_geo``
- ``token`` should be set to the access token of the logged-in account.
- ``lat`` should be the latitude of the phone, a decimal value in degrees. Positive is North, negative is South.
- ``lon`` should be the longitude of the phone, a decimal value in degrees. Positive is West, negative is East.

**Example command**

```
http://SERVER_ADDRESS/NearChat/api.json?mode=update_geo&token={token}&lat={lat}&lon={lon}
```

**Example response**

```
{
    "status":"{success | failure}",
    "reason":"{sample reason message}"
}
```

- ``status`` indicates whether the information update was successful.
- ``reason`` indicates the reason for failure if there is any.
- *Notes*: ``reason`` can be NULL/nonexistent.

# Searching for other users nearby
*Description*: Returns a JSON array of other users within the search radius.

**Required GET/POST parameters**
- ``mode`` should be set to ``search_nearby``
- ``token`` should be set to the access token of the logged-in account.
- ``lat`` should be the latitude of the phone, a decimal value in degrees. Positive is North, negative is South.
- ``lon`` should be the longitude of the phone, a decimal value in degrees. Positive is West, negative is East.
- ``radius`` should be the search radius of the query, in kilometers.

**Example command**

```
http://SERVER_ADDRESS/NearChat/api.json?mode=search_nearby&token={token}&lat={lat}&lon={lon}
```

**Example response**

```
{
    "results":[
        {
            "id":1,
            "username":"MyUsername",
            "age":18,
            "gender":"Male",
            "relationship_status":"Single",
            "bio":"Sample bio text",
            "interests":["cars","magic the gathering","rc planes","anime","hiking"],
            "telegram":"@MyTelegramUsername",
            "visible":true
        },
        {
            "id":2,
            "username":"MyUsername2",
            "age":19,
            "gender":"Male",
            "relationship_status":"Single",
            "bio":"Sample bio text",
            "interests":["cars","magic the gathering","rc planes","anime","hiking"],
            "telegram":"@MyTelegramUsername2",
            "visible":true
        }
    ],
    "status":"{success | failure}",
    "reason":"{sample reason message if failure}"
}
```

- ``results`` is a JSON array of the users within the search radius of the provided coordinates.
- ``status`` indicates whether the information update was successful.
- ``reason`` indicates the reason for failure if there is any.
- *Notes*: ``reason`` can be NULL/nonexistent.
