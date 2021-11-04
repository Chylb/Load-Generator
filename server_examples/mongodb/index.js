import Express from "express";
import fs from "fs";
import { MongoClient } from 'mongodb'

const GLOBAL_OPTIONS = {
    port: '1111'
}

const url = 'mongodb://127.0.0.1:27017'
const dbname = 'mongo-test'

const client = new MongoClient(url);
await client.connect();
const db = client.db(dbname);
const collection = db.collection('players2');

const app = Express();
app.use(Express.json());
app.use(Express.urlencoded({ extended: true }))

app.listen(GLOBAL_OPTIONS.port, () => console.log(`Server runs at http://localhost:${GLOBAL_OPTIONS.port}/ \n`));

let requestCounter = 1;

app.get("/players/:id", async (req, res) => {
    console.log(requestCounter++);
    const player = await collection.findOne({"_id": parseInt(req.params.id)});
    res.json(player);
});

const all = await collection.find({}).toArray();
if (all.length == 0) {
    fillDatabase();
}

function fillDatabase() {
    const data = fs.readFileSync('./players_20.csv', 'utf8')
    const header = data.split("\n")[0].split(",");
    const playerRows = data.split("\n");
    playerRows.shift();
    playerRows.pop();
    
    const columnIx = new Map();
    for (const [ix, columnName] of header.entries()) {
        columnIx.set(columnName, ix);
    }

    const textColumns = ["short_name", "long_name", "nationality", "club", "player_positions", "dob", "preferred_foot"];
    const numericalColumns = ["age", "height_cm", "weight_kg", "value_eur", "overall"];

    for (const [ix, line] of playerRows.entries()) {
        const rowValues = line.split(",");

        const player = {_id: ix};
        for (const column of textColumns) {
            player[column] = rowValues[columnIx.get(column)];
        }
        for (const column of numericalColumns) {
            player[column] = parseInt(rowValues[columnIx.get(column)]);
        }
        collection.insertOne(player);
    }
    //console.log(header)
}