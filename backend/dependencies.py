from fastapi import Header, HTTPException, status

async def verify_authentication_header(x_authentication: str = Header(None)):
    if x_authentication != "yes":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or missing authentication header",
        )
    return x_authentication
