import * as React from 'react';
import { useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell, { tableCellClasses } from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { collection, getDocs, query, orderBy, limit } from 'firebase/firestore';
import { db } from '../../firebase'; 

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: '#616161',
    color: theme.palette.common.white,
    fontSize: 20,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 16,
    border: 0,
  },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  '&:nth-of-type(even)': {
    backgroundColor: '#EDEDED',
  },
  '&:last-child td, &:last-child th': {
    border: 0,
  },
}));

const StyledTableHead = styled(TableHead)({
  backgroundColor: '#616161',
});

const StyledTableContainer = styled(TableContainer)({
  marginBottom: '35px',
  boxShadow: 'none',
  border: '1px #e0e0e0 solid',
});

const HomeUserTable = () => {
  const [rows, setRows] = useState([]);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const q = query(collection(db, 'users'), orderBy('createdAt', 'desc'), limit(2));
        const querySnapshot = await getDocs(q);
        const users = querySnapshot.docs.map(doc => ({
          id: doc.id,
          ...doc.data(),
          joinDate: doc.data().createdAt?.toDate().toISOString().split('T')[0]
        }));
        setRows(users);
      } catch (error) {
        console.error('Error fetching users: ', error);
      }
    };

    fetchUsers();
  }, []);

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
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </StyledTableContainer>
  );
}
export default HomeUserTable;