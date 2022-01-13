import request from "supertest";
import app from "../../../app";
const fs = require('fs');

const { encode, decode } = require('@stablelib/base64')
const jose = require('jose')

const {
    sign,
    //verify,
    generateKeyPairFromSeed,
    //extractPublicKeyFromSecretKey,
    //convertPublicKeyToX25519,
    //convertSecretKeyToX25519
} = require('@stablelib/ed25519')


beforeAll(() => {});

//TODO: set key to random value, check random value is returned 



describe("kbt Controller", () => {

	//TODO: generate and send JWT with save request. verify endpoint is hash of pubkey
	// implementation of JWT logic below

	// store and get valid data
	it("should update kbt key/value ", async () => {
		const res = await request(app).get("/updatekbt/deadbeef/reddit.com");
		expect(res.status).toEqual(200);
	});

	it("should get kbt name by kbtid", async () => {
		const res = await request(app).get("/kbt/deadbeef");
		expect(res.status).toEqual(200);
	});


	// check for bad input
	it("should not update kbt key/value ", async () => {
		const res = await request(app).get("/updatekbt/deadbeefzz/reddit.com");
		expect(res.status).toEqual(500);
	});

	it("should provide 500 error for bad hex in kbt name ", async () => {
		const res = await request(app).get("/kbt/deadbeefzzz");
		expect(res.status).toEqual(500);
	});


	it("should create and validate jwt ", async () => {
		//TODO: check for case sensitivity

		// JWT reference

		// auth keys
		const masterKeyMaterial = generateKeyPairFromSeed(random32bytes())

		var privateKey  : string = masterKeyMaterial.secretKey;
		var publicKey  : string  = masterKeyMaterial.publicKey;

		var jprivateKey = await jose.importSPKI(encode(privateKey), 'EdDSA')


		const jwt = await new jose.SignJWT({ 'urn:example:claim': true })
		  .setProtectedHeader({ alg: 'Ed25519' })
		  .setIssuedAt()
		  .setIssuer('urn:example:issuer')
		  .setAudience('urn:example:audience')
		  .setExpirationTime('2h')
		  .setSubject(publicKey)
		  .sign(jprivateKey)

		console.log(jwt)

	});
});

function random32bytes(){
	    var abc = "abcdefghijklmnopqrstuvwxyz1234567890".split("");
	    var token=""; 
	    for(var i=0;i<32;i++){
		             token += abc[Math.floor(Math.random()*abc.length)];
		        }
	    return token; //Will return a 32 bit "hash"
}
