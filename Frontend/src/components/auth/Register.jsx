// import { useState } from 'react';
// import PropTypes from 'prop-types';

// const RegisterComponent = () => {
//   const [formData, setFormData] = useState({
//     name: '',
//     lastName: '',
//     userName: '',
//     email: '',
//     password: '',
//     status: 1,         // Valor fijo
//     createUser: "REGISTER"  // Valor fijo
//   });
//   const [error, setError] = useState(null);

//   const handleChange = (e) => {
//     const { name, value } = e.target;
//     setFormData((prevData) => ({
//       ...prevData,
//       [name]: value
//     }));
//   };

//   const handleRegister = async () => {
//     try {
//       // Codificación a Base64 con todos los parámetros
//       const base64Payload = btoa(JSON.stringify(formData));

//       const response = await fetch('http://localhost:8082/auth/register', {
//         method: 'POST',
//         headers: {
//           'Content-Type': 'application/json',
//         },
//         body: base64Payload,
//       });

//       if (!response.ok) {
//         throw new Error('Network response was not ok');
//       }

//       const base64Data = await response.text();
//       const jsonString = atob(base64Data);
//       const data = JSON.parse(jsonString);

//       console.log('Registro exitoso:', data);
//     } catch (error) {
//       console.error('Error en el registro:', error);
//       setError('Error en el registro');
//     }
//   };

//   return (
//     <div>
//       <h1>Registro de Usuario</h1>
//       {error && <p style={{ color: 'red' }}>{error}</p>}
      
//       <form>
//         <input
//           type="text"
//           name="name"
//           placeholder="Nombre"
//           value={formData.name}
//           onChange={handleChange}
//         />
//         <input
//           type="text"
//           name="lastName"
//           placeholder="Apellido"
//           value={formData.lastName}
//           onChange={handleChange}
//         />
//         <input
//           type="text"
//           name="userName"
//           placeholder="Nombre de usuario"
//           value={formData.userName}
//           onChange={handleChange}
//         />
//         <input
//           type="email"
//           name="email"
//           placeholder="Correo electrónico"
//           value={formData.email}
//           onChange={handleChange}
//         />
//         <input
//           type="password"
//           name="password"
//           placeholder="Contraseña"
//           value={formData.password}
//           onChange={handleChange}
//         />
//         <button type="button" onClick={handleRegister}>
//           Registrar
//         </button>
//       </form>
//     </div>
//   );
// };

// RegisterComponent.propTypes = {
//   onRegisterSuccess: PropTypes.func,
// };

// export default RegisterComponent;
