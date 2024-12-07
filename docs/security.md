# Generating JWKS (JSON Web Key Set)

This guide will help you generate RSA keys and create a JSON Web Key Set (JWKS).

ALTERNATIVE -- build file on https://pem2jwk.vercel.app/

## Step 1: Generate the RSA Private Key

Run the following command to generate an RSA private key:

```bash
openssl genpkey -algorithm RSA -out private-key.pem -pkeyopt rsa_keygen_bits:2048
```
This command will create a private-key.pem file with a 2048-bit RSA private key.

## Step 2: Generate the Public Key
Now, generate the corresponding public key from the private key by running:

```bash
openssl rsa -in private-key.pem -pubout -out public-key.pem
```
This will create a public-key.pem file containing the public key.

## Step 3: Export Public Key Modulus
Next, export the modulus from the public key:

```bash
openssl rsa -pubin -in public-key.pem -modulus -noout
```

## Step 4: Convert Modulus to Base64 and same with exponent

```bash
echo -n "MODULUS_STRING" | xxd -r -p | base64 | tr '+/' '-_' | tr -d '='
```

## Final step: Create jwks.json file

```json
{
  "keys": [
    {
      "kty": "RSA",
      "kid": "unique-key-id",
      "use": "sig",
      "n": "BASE64_URL_MODULUS",
      "e": "BASE64_URL_EXPONENT"
    }
  ]
}

```





