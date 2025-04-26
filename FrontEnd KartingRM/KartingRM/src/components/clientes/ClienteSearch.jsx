import React, { useState } from 'react';
import { 
  getClienteById, 
  getClienteByRut,
  getClientesByNombreApellido 
} from '../../services/clienteService';
import './ClienteSearch.css';

const ClienteSearch = ({ onSearchResults }) => {
  const [searchType, setSearchType] = useState('id');
  const [searchValue, setSearchValue] = useState('');
  const [nombre, setNombre] = useState('');
  const [apellido, setApellido] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    
    if (searchType !== 'nombre' && !searchValue.trim()) {
      setError('Por favor ingrese un valor para buscar');
      return;
    }

    if (searchType === 'nombre' && (!nombre.trim() || !apellido.trim())) {
      setError('Por favor ingrese nombre y apellido');
      return;
    }

    setIsSearching(true);
    setError('');

    try {
      let result;
      switch(searchType) {
        case 'id':
          result = await getClienteById(searchValue);
          break;
        case 'rut':
          result = await getClienteByRut(searchValue);
          break;
        case 'nombre':
          result = await getClientesByNombreApellido(nombre, apellido);
          break;
        default:
          throw new Error('Tipo de búsqueda no válido');
      }
      
      onSearchResults(Array.isArray(result) ? result : [result]);
    } catch (err) {
      setError(searchType === 'nombre' ? 
        'No se encontraron clientes con ese nombre y apellido' : 
        'Cliente no encontrado');
      onSearchResults([]);
    } finally {
      setIsSearching(false);
    }
  };

  const handleReset = () => {
    setSearchValue('');
    setNombre('');
    setApellido('');
    setError('');
    onSearchResults(null);
  };

  return (
    <div className="cliente-search">
      <h3>Buscar Cliente</h3>
      <form onSubmit={handleSearch}>
        <div className="search-controls">
          <div className="search-type">
            <label>
              <input
                type="radio"
                value="id"
                checked={searchType === 'id'}
                onChange={() => setSearchType('id')}
              />
              Por ID
            </label>
            <label>
              <input
                type="radio"
                value="rut"
                checked={searchType === 'rut'}
                onChange={() => setSearchType('rut')}
              />
              Por RUT
            </label>
            <label>
              <input
                type="radio"
                value="nombre"
                checked={searchType === 'nombre'}
                onChange={() => setSearchType('nombre')}
              />
              Por Nombre/Apellido
            </label>
          </div>

          {searchType !== 'nombre' ? (
            <div className="search-input">
              <input
                type={searchType === 'id' ? 'number' : 'text'}
                value={searchValue}
                onChange={(e) => setSearchValue(e.target.value)}
                placeholder={
                  searchType === 'id' ? 
                  'Ingrese ID' : 
                  'Ingrese RUT (ej: 12.345.678-9)'
                }
              />
              <button type="submit" disabled={isSearching || !searchValue.trim()}>
                {isSearching ? 'Buscando...' : 'Buscar'}
              </button>
            </div>
          ) : (
            <div className="name-search-inputs">
              <input
                type="text"
                value={nombre}
                onChange={(e) => setNombre(e.target.value)}
                placeholder="Nombre"
              />
              <input
                type="text"
                value={apellido}
                onChange={(e) => setApellido(e.target.value)}
                placeholder="Apellido"
              />
              <button 
                type="submit" 
                disabled={isSearching || !nombre.trim() || !apellido.trim()}
              >
                {isSearching ? 'Buscando...' : 'Buscar'}
              </button>
            </div>
          )}

          <button 
            type="button" 
            onClick={handleReset}
            className="reset-button"
          >
            Mostrar Todos
          </button>
        </div>
      </form>

      {error && <div className="error-message">{error}</div>}
    </div>
  );
};

export default ClienteSearch;