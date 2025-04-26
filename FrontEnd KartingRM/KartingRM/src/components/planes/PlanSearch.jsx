import React, { useState } from 'react';
import { getPlanById } from '../../services/planService';
import './PlanSearch.css';

const PlanSearch = ({ onSearchResults }) => {
  const [searchValue, setSearchValue] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    
    if (!searchValue.trim()) {
      setError('Por favor ingrese un ID de plan');
      return;
    }

    setIsSearching(true);
    setError('');

    try {
      const result = await getPlanById(searchValue);
      onSearchResults([result]);
    } catch (err) {
      setError('Plan no encontrado');
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
    <div className="plan-search">
      <h3>Buscar Plan</h3>
      <form onSubmit={handleSearch}>
        <div className="search-controls">
          <div className="search-input">
            <input
              type="number"
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
              placeholder="Ingrese ID del plan"
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

export default PlanSearch;