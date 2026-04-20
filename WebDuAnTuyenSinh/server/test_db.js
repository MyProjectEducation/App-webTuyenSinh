const db = require('./config/db');

async function checkConnection() {
    try {
        const [rows] = await db.query('SELECT 1 as result');
        console.log("DATABASE_CONNECTION_SUCCESS");
        
        const [users] = await db.query('SELECT COUNT(*) as count FROM sys_users');
        console.log("TOTAL_USERS: " + users[0].count);
        process.exit(0);
    } catch(err) {
        console.error("DATABASE_CONNECTION_ERROR");
        console.error(err);
        process.exit(1);
    }
}
checkConnection();
