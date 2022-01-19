import { Request, Response } from "express";
import { param, validationResult } from "express-validator";

const sha256 = require('@stablelib/sha256')
const { encodeURLSafe, decodeURLSafe } = require('@stablelib/base64')

const jwt = require('jsonwebtoken');
var Hypercore = require('hypercore')
var feed = new Hypercore('./my-first-dataset', {valueEncoding: 'utf-8'})

const Hyperbee = require('hyperbee')
const db = new Hyperbee(feed, {
	keyEncoding: 'utf-8', // can be set to undefined (binary), utf-8, ascii or and abstract-encoding
	valueEncoding: 'binary' // same options as above
})

export const registerAccount = async (req: Request, res: Response) => {
	// add public key to user registry 
	try {
		const token : string = req.body.jwt;
		var verifyOptions = {
			expiresIn:  "12h",
			algorithm:  ["RS256"]
		};

		// extract the public key from the payload and use it to verify
		var decoded = jwt.decode(token);
		var extractedPK = decoded.pubkey;
		var legit = jwt.verify(token, extractedPK, verifyOptions);
		var publickey = legit.pubkey

		var today = new Date(Date.now())

		// store the public key in hypercore using <hash>_key as index for validation later
		var pkhash = encodeURLSafe(sha256.hash(publickey))
		await db.put(pkhash + "_key",  publickey);
		await db.put(pkhash + "_created", today.toUTCString());
		await db.put(pkhash + "_updated", today.toUTCString());

		res.status(200).json({ success: true, operation: 'register', 'publickey_hash': pkhash});
	} catch {
		// error
		//console.log("bad token");
		res.status(500).json({success: false, error: "invalid token"});
	}


}

export const postSaveNameByKbtId = async (req: Request, res: Response) => {

	let kbtname : string = ""
	let kbtendpoint : string = ""

	try {
		const token : string = req.body.jwt;
		//console.log(token);
		var verifyOptions = {
			expiresIn:  "12h",
			algorithm:  ["RS256"]
		};

		// extract the public key from the payload and use it to verify
		var decoded = jwt.decode(token);
		var extractedPK = decoded.pubkey;

		var today = new Date(Date.now())



		var legit = jwt.verify(token, extractedPK, verifyOptions);
		kbtname = legit.kbtname
		kbtendpoint = legit.endpoint
		var pkhash = encodeURLSafe(sha256.hash(legit.pubkey))

		//console.log("saving " + kbtendpoint + " to name key " + kbtname);
		await db.put(kbtname, kbtendpoint);
		await db.put(pkhash + "_updated", today.toUTCString());
		//console.log("\nJWT verification result: " + JSON.stringify(legit));

		res.status(200).json({ success: true, operation: 'save', 'kbtname': kbtname, endpoint: kbtendpoint});
	} catch {
		// error
		//console.log("bad token");
		res.status(500).json({success: false, error: "invalid token"});
	}


};


export const getNameByKbtId = async (req: Request, res: Response) => {

	const kbtId : string = (req.params.kbtid).toString();

	const node = await db.get(kbtId) 
	if(node !== null) {
		//console.log(node) 
		res.status(200).json({ success: true, operation: 'lookup', result: node.value.toString(), params: kbtId});
	} else {
		res.status(500).json({ success: false, error: "invalid kbt name"});
	}
};

function is_hex(str : string) {
	for (const c of str) {
		if ("0123456789ABCDEFabcdef".indexOf(c) === -1) {
			return false;
		}
	}
	return true;
}

