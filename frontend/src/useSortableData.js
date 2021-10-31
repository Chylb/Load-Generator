import { useMemo, useState } from "react";

export const useSortableData = (items, config) => {
    const [sortConfig, setSortConfig] = useState(config);

    const sortedItems = useMemo(() => {
        let sortableItems = [...items];
        sortableItems.sort((a, b) => {
            let aVal = a;
            let bVal = b;
            for (let key of sortConfig.key.split(".")) {
                aVal = aVal[key];
            }
            for (let key of sortConfig.key.split(".")) {
                bVal = bVal[key];
            }

            if (aVal < bVal) {
                return sortConfig.direction === 'ascending' ? -1 : 1;
            }
            if (aVal > bVal) {
                return sortConfig.direction === 'ascending' ? 1 : -1;
            }
            return 0;
        });
        return sortableItems;
    }, [items, sortConfig]);

    const requestSort = (key) => {
        let direction = 'ascending';
        if (sortConfig && sortConfig.key === key && sortConfig.direction === 'ascending') {
            direction = 'descending';
        }
        setSortConfig({ key, direction });
    }

    return [sortedItems, requestSort, sortConfig];
}