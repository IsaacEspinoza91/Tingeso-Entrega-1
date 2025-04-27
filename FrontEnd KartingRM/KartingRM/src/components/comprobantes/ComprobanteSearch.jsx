import React, { useState } from 'react';
import { getComprobanteById } from '../../services/comprobanteService';
import './ComprobanteSearch.css';

const ComprobanteSearch = ({ onSearchResults }) => {
  const [searchValue, setSearchValue] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    
    if (!searchValue.trim()) {
      setError('Por favor ingrese un ID de comprobante');
      return;
    }

    setIsSearching(true);
    setError('');

    try {
      const result = await getComprobanteById(searchValue);
      onSearchResults([result]);
    } catch (err) {
      setError('Comprobante no encontrado');
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
    <div className="comprobante-search">
      <h3>Buscar Comprobante</h3>
      <form onSubmit={handleSearch}>
        <div className="search-controls">
          <div className="search-input">
            <input
              type="number"
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
              placeholder="Ingrese ID del comprobante"
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

export default ComprobanteSearch;