import mysql from 'mysql2/promise';
import fs from 'fs';

async function generateDatabaseArtifact() {
  // Aiven Database Credentials
  const config = {
    host: 'mysql-2c7f8a42-munimunindra928-b7ee.d.aivencloud.com',
    port: 22277,
    user: 'avnadmin',
    password: process.env.DB_PASSWORD,
    database: 'repsync_db',
    ssl: { rejectUnauthorized: false }
  };

  let markdown = '# 🗄️ Live Aiven Database Viewer\n\n';
  markdown += 'This document pulls your actual live data from the cloud MySQL database.\n\n';

  let connection;
  try {
    connection = await mysql.createConnection(config);
    console.log('Connected to Aiven Database.');

    // Get all tables
    const [tables] = await connection.query('SHOW TABLES');
    
    for (const row of tables) {
      const tableName = Object.values(row)[0];
      markdown += `## 📋 Table: \`${tableName}\`\n\n`;
      
      const [tableData] = await connection.query(`SELECT * FROM ${tableName} LIMIT 50`);
      
      if (tableData.length === 0) {
        markdown += '*This table is currently empty.*\n\n';
        continue;
      }
      
      // Build Markdown Table
      const headers = Object.keys(tableData[0]);
      markdown += '| ' + headers.join(' | ') + ' |\n';
      markdown += '| ' + headers.map(() => '---').join(' | ') + ' |\n';
      
      tableData.forEach(dataRow => {
        const values = headers.map(h => {
          let val = dataRow[h];
          if (val === null) return '`NULL`';
          if (typeof val === 'object') return val.toISOString ? val.toISOString().split('T')[0] : JSON.stringify(val);
          // escape markdown pipes
          return String(val).replace(/\|/g, '\\|').substring(0, 100);
        });
        markdown += '| ' + values.join(' | ') + ' |\n';
      });
      markdown += '\n---\n\n';
    }

    fs.writeFileSync('C:/Users/munin/.gemini/antigravity-ide/brain/8d01cd23-932a-4114-b0c6-fe220ca691ae/database_viewer.md', markdown);
    console.log('Generated database_viewer.md');
  } catch (error) {
    console.error('Error connecting to database:', error);
    fs.writeFileSync('C:/Users/munin/.gemini/antigravity-ide/brain/8d01cd23-932a-4114-b0c6-fe220ca691ae/database_viewer.md', '# Error\n\nCould not connect to database:\n```\n' + error.message + '\n```');
  } finally {
    if (connection) {
      await connection.end();
    }
  }
}

generateDatabaseArtifact();
