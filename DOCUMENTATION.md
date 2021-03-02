# NearChat JSON API Server Documentation
The NearChat JSON API server is a flexible, modular and secure data storage, management and exchange system that encompasses secure access control to NearChat informational assets, storage and retrieval of NearChat account asset and lifecycle information, in simple, machine-parsable and language-agnostic JSON (Javascript Object notation, see [here](http://www.json.org/fatfree.html) for a brief overview) format.

# General command specification
The AIBaby JSON server can be accessed from the URL ``http://SERVER_ADDRESS/NearChat/api.json`` for authentication and data exchange. Any request to the server consists of the GET/POST parameters ``mode`` to specify the action to be performed on the server and ``token`` to specify the access token required for authorization to perform restricted actions (everything except for login), as well as additional parameters specific to the action specified.

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
    ERR_CREATIONTYPE_MISSING,
    ERR_CREATIONTYPE_UNRECOGNIZED,
    ERR_MODE_NOT_RECOGNIZED,
    ERR_MODE_EMPTY,
    ERR_LOGIN_UNSUCCESSFUL,
    ERR_ACCOUNT_NONEXISTANT
```

