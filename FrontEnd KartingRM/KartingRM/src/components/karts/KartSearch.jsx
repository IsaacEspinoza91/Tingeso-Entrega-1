import React, { useState } from 'react';
import { getKartById } from '../../services/kartService';
import './KartSearch.css';

const KartSearch = ({ onSearchResults }) => {
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
        result = await getKartById(searchValue);
      }
      
      onSearchResults(Array.isArray(result) ? result : [result]);
    } catch (err) {
      setError('Kart no encontrado');
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
    <div className="kart-search">
      <h3>Buscar Kart</h3>
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
          </div>

          <div className="search-input">
            <input
              type={searchType === 'id' ? 'number' : 'text'}
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
              placeholder={searchType === 'id' ? 'Ingrese ID del kart' : 'Ingrese modelo'}
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

export default KartSearch;