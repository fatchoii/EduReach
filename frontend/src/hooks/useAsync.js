import { useEffect, useState } from 'react';

// Runs an async function and tracks its loading / error / data state.
export function useAsync(fn, deps = []) {
  const [state, setState] = useState({ data: null, loading: true, error: null });

  useEffect(() => {
    let active = true;
    setState({ data: null, loading: true, error: null });
    fn()
      .then((data) => active && setState({ data, loading: false, error: null }))
      .catch((error) =>
        active && setState({ data: null, loading: false, error: error.message || 'Something went wrong.' })
      );
    return () => {
      active = false;
    };
  }, deps);

  return state;
}
