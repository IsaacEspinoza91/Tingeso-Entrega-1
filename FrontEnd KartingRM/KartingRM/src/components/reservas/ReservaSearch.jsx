import React, { useState } from 'react';
import { getReservaById, getReservasByCliente } from '../../services/reservaService';
import './ReservaSearch.css';

const ReservaSearch = ({ onSearchResults }) => {
  const [searchType, setSearchType] = useState('id');
  const [searchValue, setSearchValue] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    
    if (!searchValue.trim()) {
      setError('Por favor ingrese un valor para buscar');
      return;
    }

    setIsSearching(true);
    setError('');

    try {
      let result;
      if (searchType === 'id') {
        result = await getReservaById(searchValue);
        onSearchResults([result]);
      } else {
        result = await getReservasByCliente(searchValue);
        onSearchResults(result);
      }
    } catch (err) {
      setError(searchType === 'id' ? 'Reserva no encontrada' : 'No se encontraron reservas para este cliente');
      onSearchResults([]);
    } finally {
      setIsSearching(false);
    }
  };

  const handleReset = () => {
    setSearchValue('');
    setError('');
    onSearchResults(null);
  };

  return (
    <div className="reserva-search">
      <h3>Buscar Reserva</h3>
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
              Por ID Reserva
            </label>
            <label>
              <input
                type="radio"
                value="cliente"
                checked={searchType === 'cliente'}
                onChange={() => setSearchType('cliente')}
              />
              Por ID Cliente
            </label>
          </div>

          <div className="search-input">
            <input
              type="number"
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
              placeholder={searchType === 'id' ? 'Ingrese ID de reserva' : 'Ingrese ID de cliente'}
              min="1"
            />
            <button type="submit" disabled={isSearching || !searchValue.trim()}>
              {isSearching ? 'Buscando...' : 'Buscar'}
            </button>
            <button 
              type="button" 
              onClick={handleReset}
              className="reset-button"
            >
              Mostrar Todos
            </button>
          </div>
        </div>
      </form>

      {error && <div className="error-message">{error}</div>}
    </div>
  );
};

export default ReservaSearch;