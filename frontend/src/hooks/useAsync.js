import { useCallback, useEffect, useRef, useState } from 'react';

/**
 * Runs an async function and tracks loading / error / data.
 * - Re-runs (with a loading state) whenever deps change.
 * - refreshMs re-runs it quietly in the background, so numbers update without flicker.
 * - reload() re-runs it quietly, for example after saving something.
 */
export function useAsync(fn, deps = [], { refreshMs = 0 } = {}) {
  const [state, setState] = useState({ data: null, loading: true, error: null });
  const fnRef = useRef(fn);
  fnRef.current = fn;
  const latest = useRef(0);

  const run = useCallback((quiet) => {
    const id = ++latest.current;
    if (!quiet) setState({ data: null, loading: true, error: null });
    fnRef
      .current()
      .then((data) => {
        if (id === latest.current) setState({ data, loading: false, error: null });
      })
      .catch((error) => {
        if (id !== latest.current) return;
        setState((prev) =>
          quiet && prev.data
            ? prev
            : { data: null, loading: false, error: error.message || 'Something went wrong.' }
        );
      });
  }, []);

  useEffect(() => {
    run(false);
    return () => {
      latest.current += 1;
    };
  }, deps);

  useEffect(() => {
    if (!refreshMs) return undefined;
    const timer = setInterval(() => run(true), refreshMs);
    return () => clearInterval(timer);
  }, [refreshMs, run]);

  const reload = useCallback(() => run(true), [run]);
  return { ...state, reload };
}
