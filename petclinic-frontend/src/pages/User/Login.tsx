import { useState, FormEvent } from 'react';
import axios from 'axios';
import { UserResponseModel } from '@/shared/models/UserResponseModel';
import { useSetUser } from '@/context/UserContext.tsx';
import { Link, useNavigate } from 'react-router-dom';
import { AppRoutePaths } from '@/shared/models/path.routes.ts';
import anotherDoctorAndDoggy from '@/assets/Login/another-doctor-and-doggy.jpg';
import doctorAndDoggy from '@/assets/Login/doctor-and-doggy.jpg';
import doggyAndKitty from '@/assets/Login/doggy-and-kitty.jpg';
import kitty from '@/assets/Login/kitty.jpg';
import sadDoggy from '@/assets/Login/sad-doggy.jpg';
import './Login.css';
import Slideshow from './Slideshow';
import { Alert } from 'react-bootstrap';

const images = [
  doctorAndDoggy,
  anotherDoctorAndDoggy,
  doggyAndKitty,
  kitty,
  sadDoggy,
];

export default function Login(): JSX.Element {
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const setUser = useSetUser();
  const navigate = useNavigate();
  const login = async (event: FormEvent<HTMLFormElement>): Promise<void> => {
    event.preventDefault();
    setErrorMessage(null);

    const form = event.currentTarget;
    const formElements = form.elements as typeof form.elements & {
      emailInput: HTMLInputElement;
      passwordInput: HTMLInputElement;
    };

    try {
      const response = await axios.post<UserResponseModel>(
        'http://localhost:8080/api/gateway/users/login',
        {
          email: formElements.emailInput.value,
          password: formElements.passwordInput.value,
        }
      );

      setUser(response.data);
      if (response.data.userId !== '') {
        navigate(AppRoutePaths.Home);
      }
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 401) {
        setErrorMessage('Invalid email or password. Please try again.');
      } else {
        setErrorMessage('Something went wrong, oops!');
      }
    }
  };

  return (
    <div className="login-page">
      <Link to="/home" className="back-button">
        Back
      </Link>
      <div className="login-container">
        <h1>User Login</h1>
        {errorMessage && <Alert variant="danger">{errorMessage}</Alert>}
        <form onSubmit={login}>
          <label htmlFor="emailInput"></label>
          <input type="text" id="emailInput" placeholder="Enter your email" />
          <br />
          <label htmlFor="passwordInput"></label>
          <input
            type="password"
            id="passwordInput"
            placeholder="Enter your password"
          />
          <br />
          <button type="submit" className="login-button">
            Login
          </button>
        </form>
      </div>
      <Slideshow images={images} interval={7000} />
    </div>
  );
}
