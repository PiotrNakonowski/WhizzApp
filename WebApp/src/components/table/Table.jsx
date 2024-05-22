import * as React from 'react';
import { styled } from '@mui/material/styles';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell, { tableCellClasses } from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import Button from '@mui/material/Button';

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: '#616161',
    color: theme.palette.common.white,
    fontSize: 20,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 16,
  },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  '&:nth-of-type(odd)': {
    backgroundColor: theme.palette.action.hover,
  },
  // hide last border
  '&:last-child td, &:last-child th': {
    border: 0,
  },
}));

const StyledTableHead = styled(TableHead)({
  backgroundColor: '#616161',
});

const StyledTableContainer = styled(TableContainer)({
  // boxShadow: 'none', // Usuwa cień - zakomentowane
  marginBottom: '20px', // Dodaje margines od dołu
});

// Definicja danych do wyświetlenia w tabeli
const rows = [
  createData(1, 'John', 'Doe', 'john.doe@example.com', '2023-10-15'),
  createData(2, 'Jane', 'Smith', 'jane.smith@example.com', '2023-08-20'),
];

// Funkcja do tworzenia danych użytkowników
function createData(id, name, surname, email, joinDate) {
  return { id, name, surname, email, joinDate };
}

export default function CustomizedTables() {
  const handleDelete = (id) => {
    // Tutaj możesz dodać logikę usuwania użytkownika o podanym ID
    console.log(`Usuń użytkownika o ID: ${id}`);
  };

  return (
    <StyledTableContainer component={Paper}>
      <Table sx={{ minWidth: 700 }} aria-label="customized table">
        <StyledTableHead>
          <TableRow>
            <StyledTableCell>Id_user</StyledTableCell>
            <StyledTableCell align="right">Imię</StyledTableCell>
            <StyledTableCell align="right">Nazwisko</StyledTableCell>
            <StyledTableCell align="right">Email</StyledTableCell>
            <StyledTableCell align="right">Data dołączenia</StyledTableCell>
            <StyledTableCell align="right" style={{ paddingRight: '32px' }}>Akcja</StyledTableCell>
          </TableRow>
        </StyledTableHead>
        <TableBody>
          {rows.map((row) => (
            <StyledTableRow key={row.id}>
              <StyledTableCell component="th" scope="row">
                {row.id}
              </StyledTableCell>
              <StyledTableCell align="right">{row.name}</StyledTableCell>
              <StyledTableCell align="right">{row.surname}</StyledTableCell>
              <StyledTableCell align="right">{row.email}</StyledTableCell>
              <StyledTableCell align="right">{row.joinDate}</StyledTableCell>
              <StyledTableCell align="right">
                <Button
                  variant="contained"
                  style={{ backgroundColor: '#D87648', color: 'white' }}
                  onClick={() => handleDelete(row.id)}
                >
                  Usuń
                </Button>
              </StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </StyledTableContainer>
  );
}
