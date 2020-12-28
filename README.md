[![Build Status](https://travis-ci.com/schonacin/android-auth.svg?token=QGKnygSyxwYQVgBaGnE7&branch=master)](https://travis-ci.com/schonacin/android-auth)
[![codecov](https://codecov.io/gh/schonacin/android-auth/branch/master/graph/badge.svg?token=uy499RGNh8)](https://codecov.io/gh/schonacin/android-auth)

# android-auth
An Android library to enable usage of smartphones as FIDO2 authenticators via BLE, using RxJava3.
This repository also contains a prototype implementing continuous authentication mechanism based on FIDO2, which can be found on the ["fidonuous_demo_app" branch](https://github.com/schonacin/android-auth/tree/fidonuous_demo_app).

## Development Status
Currently only the BLE transport of the CTAP2 specification is implemented, but the architecture allows for easy extension to add support for the USB and NFC transports.
We also did not implement the complete Authenticator API described in the CTAP2 specifications, so this project is far from finished and should definitely not be used in any production environment.

## Compatibility
For general information on compatibility of different operating systems, browsers and CTAP2 transports (USB/NFC/BLE), see [this repo](https://github.com/apowers313/fido2-webauthn-status). 
We successfully tested the Android authenticator with an up-to-date Firefox on Windows 10. For other setups we cannot guarantee that it works (mostly due to bluetooth related issues) and even the tested setup might not work in the future due to changes by Mozilla or Microsoft in their products.

## Usage
We implement an Android service called ```FidoAuthService``` that encapsulates the behavior described by the CTAP2 specification. If the app using this service checks for user approval (e.g. via button press or using a fingerprint reader), this can be done by reacting to an intent coming from the service. See the ["fidonuous_demo_app" branch](https://github.com/schonacin/android-auth/tree/fidonuous_demo_app) for an example.
For an overview on the library architecture and the general data flow, see [this wiki page](https://github.com/schonacin/android-auth/wiki/CTAP2-Flow-visualization).

## Citation
If you use this code for your research, please cite our paper "FIDOnuous: A FIDO2/WebAuthN Extension to Support Continuous Web Authentication".
