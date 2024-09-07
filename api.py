from flask import Flask, jsonify
from flask_cors import CORS  # Import Flask-CORS
import mysql.connector
from mysql.connector import Error
from dotenv import load_dotenv
import os

app = Flask(__name__)
CORS(app, resources={r"/api/*": {"origins": "*"}})

# Load environment variables from .env
load_dotenv()

# Read database credentials from environment variables
DB_HOST = os.getenv('DB_HOST')
DB_USERNAME = os.getenv('DB_USERNAME')
DB_PASSWORD = os.getenv('DB_PASSWORD')
DB_NAME = os.getenv('DB_NAME')

# Connect to MySQL database
def connect_db():
    try:
        conn = mysql.connector.connect(
            host=DB_HOST,
            user=DB_USERNAME,
            password=DB_PASSWORD,
            database=DB_NAME
        )
        if conn.is_connected():
            return conn
    except Error as e:
        print(f"Error connecting to MySQL: {e}")
        return None

# API to get the invited players and total invites for a specific player
@app.route('/api/invites/<player>', methods=['GET'])
def get_invites(player):
    try:
        conn = connect_db()
        if conn:
            cursor = conn.cursor()

            # Query to get the invited players by the inviter
            cursor.execute("SELECT invitee FROM invitations WHERE inviter = %s", (player,))
            invitees = cursor.fetchall()

            # Prepare the response
            invitee_list = [invitee[0] for invitee in invitees]  # Extract invitees from tuples
            total_invites = len(invitee_list)

            cursor.close()
            conn.close()

            # Send response as JSON
            response = {
                "total_invites": total_invites,
                "invited_players": invitee_list
            }
            return jsonify(response), 200
        else:
            return jsonify({"error": "Failed to connect to the database"}), 500

    except Error as e:
        return jsonify({"error": f"Database error: {e}"}), 500

# Run Flask app
if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=9999)
