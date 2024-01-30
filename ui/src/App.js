import React, { useState } from "react";
import axios from "axios";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";

const API_URL = "http://localhost:3030/api"; // Replace with your actual API URL

const InputField = ({ placeholder, type, onChange }) => (
  <input
    type={type}
    placeholder={placeholder}
    className="form-control mb-2"
    onChange={onChange}
  />
);

const Button = ({ onClick, styleClass, text }) => (
  <button className={`btn ${styleClass}`} onClick={onClick}>
    {text}
  </button>
);

const AddUserPage = ({ setUsername, setPassword, handleAddUser }) => (
  <div className="mb-4">
    <div class="username-requirements">
      <p>Username & Password requirements:</p>
      <ul>
        <li>Length: Between 3 and 50 characters</li>
      </ul>
    </div>
    <div class="password-requirements">
      <p>Password requirements:</p>
      <ul>
        <li>At least one uppercase letter</li>
        <li>At least one lowercase letter</li>
        <li>At least one digit</li>
        <li>At least one special character</li>
      </ul>
    </div>
    <h2>Add User</h2>
    <InputField
      type="text"
      placeholder="Username"
      onChange={(e) => setUsername(e.target.value)}
    />
    <InputField
      type="password"
      placeholder="Password"
      onChange={(e) => setPassword(e.target.value)}
    />
    <Button styleClass="btn-success" onClick={handleAddUser} text="Add User" />
  </div>
);

const LoginPage = ({ setUsername, setPassword, handleLogin }) => (
  <div className="mb-4">
    <h2>Login</h2>
    <InputField
      type="text"
      placeholder="Username"
      onChange={(e) => setUsername(e.target.value)}
    />
    <InputField
      type="password"
      placeholder="Password"
      onChange={(e) => setPassword(e.target.value)}
    />
    <Button styleClass="btn-primary" onClick={handleLogin} text="Login" />
  </div>
);

const RemoveAddGetBookPage = ({
  handleAddBook,
  handleRemoveBook,
  handleGetBooks,
  setTitle,
  setAuthor,
  setYear,
  setBookId,
  books,
}) => (
  <div className="mb-4">
    <h2>Get Books</h2>
    <Button
      styleClass="btn-secondary"
      onClick={handleGetBooks}
      text="Get Books"
    />

    {books && books.length > 0 && (
      <div>
        <h3>Book List</h3>
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Author</th>
              <th>Year</th>
            </tr>
          </thead>
          <tbody>
            {books.map((book) => (
              <tr key={book.id}>
                <td>{book.id}</td>
                <td>{book.title}</td>
                <td>{book.author}</td>
                <td>{book.year}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    )}
    <h2>Add Book</h2>
    <InputField
      type="text"
      placeholder="Title"
      onChange={(e) => setTitle(e.target.value)}
    />
    <InputField
      type="text"
      placeholder="Author"
      onChange={(e) => setAuthor(e.target.value)}
    />
    <InputField
      type="number"
      placeholder="Year"
      onChange={(e) => setYear(e.target.value)}
    />
    <Button styleClass="btn-info" onClick={handleAddBook} text="Add Book" />

    <h2>Remove Book</h2>
    <InputField
      type="number"
      placeholder="Book ID"
      onChange={(e) => setBookId(e.target.value)}
    />
    <Button
      styleClass="btn-danger"
      onClick={handleRemoveBook}
      text="Remove Book"
    />
  </div>
);

const App = () => {
  const [currentPage, setCurrentPage] = useState("addUser");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [title, setTitle] = useState("");
  const [author, setAuthor] = useState("");
  const [year, setYear] = useState("");
  const [bookId, setBookId] = useState("");
  const [token, setToken] = useState("");
  const [books, setBooks] = useState([]);

  const handleAddUser = async () => {
    try {
      const formData = new FormData();
      formData.append("username", username);
      formData.append("password", password);

      const response = await axios.post(`${API_URL}/user/add`, formData, {
        withCredentials: true,
      });

      console.log(response.data);
      alert("User added successfully!");

      // After successful user addition, navigate to the login page
      handlePageChange("login");
    } catch (error) {
      console.error("Add User error:", error.response.data);
    }
  };

  const handleLogin = async () => {
    try {
      const formData = new FormData();
      formData.append("username", username);
      formData.append("password", password);

      const response = await axios.post(`${API_URL}/login`, formData, {
        withCredentials: true,
      });

      console.log(response.data);
      setToken(response.data);
      alert(`Welcome, ${response.data}!`);

      handlePageChange("removeAddGetBook");
    } catch (error) {
      console.error("Login error:", error.response.data);
      alert(`Login Failed!\nError: ${error.response.data}`);
    }
  };

  const handleAddBook = async () => {
    try {
      const formData = new FormData();
      formData.append("title", title);
      formData.append("author", author);
      formData.append("year", year);

      const response = await axios.post(`${API_URL}/book/add`, formData, {
        withCredentials: true,
      });

      console.log(response.data);

      // Show alert based on the response
      if (response.data) {
        alert("Book added successfully!");
      } else {
        alert(`Failed to add book. Error: ${response.data.message}`);
      }
    } catch (error) {
      console.error("Add Book error:", error.response.data);
    }
  };

  const handleRemoveBook = async () => {
    try {
      const formData = new FormData();
      formData.append("bookId", bookId);

      const response = await axios.post(`${API_URL}/book/remove`, formData, {
        withCredentials: true,
      });

      console.log(response.data);

      // Show alert based on the response
      if (response.data) {
        alert("Book removed successfully!");
      } else {
        alert(`Failed to remove book. Error: ${response.data.message}`);
      }
    } catch (error) {
      console.error("Remove Book error:", error.response.data);
    }
  };

  const handleGetBooks = async () => {
    try {
      const response = await axios.get(`${API_URL}/book/list`, {
        withCredentials: true,
      });

      console.log(response.data);

      // Update the state with the retrieved books
      setBooks(response.data);
    } catch (error) {
      console.error("Get Books error:", error.response.data);
    }
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  return (
    <div className="container mt-5">
      <h1 className="mb-4">React UI for Spring Security API</h1>

      {/* Navigation Bar */}
      <nav className="mb-4">
        <Button
          styleClass={`btn-secondary mr-2 ${
            currentPage === "addUser" ? "active" : ""
          }`}
          onClick={() => handlePageChange("addUser")}
          text="Add User"
        />
        <Button
          styleClass={`btn-secondary mr-2 ${
            currentPage === "login" ? "active" : ""
          }`}
          onClick={() => handlePageChange("login")}
          text="Login"
        />
        <Button
          styleClass={`btn-secondary ${
            currentPage === "removeAddGetBook" ? "active" : ""
          }`}
          onClick={() => handlePageChange("removeAddGetBook")}
          text="Books Options"
        />
      </nav>

      {currentPage === "addUser" && (
        <AddUserPage
          setUsername={setUsername}
          setPassword={setPassword}
          handleAddUser={handleAddUser}
        />
      )}

      {currentPage === "login" && (
        <LoginPage
          setUsername={setUsername}
          setPassword={setPassword}
          handleLogin={handleLogin}
        />
      )}

      {currentPage === "removeAddGetBook" && (
        <RemoveAddGetBookPage
          handleGetBooks={handleGetBooks}
          handleAddBook={handleAddBook}
          handleRemoveBook={handleRemoveBook}
          setTitle={setTitle}
          setAuthor={setAuthor}
          setYear={setYear}
          setBookId={setBookId}
          books={books}
        />
      )}
    </div>
  );
};

export default App;
