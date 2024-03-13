//Description de la fonction : Récupère et affiche de manière asynchrone les informations sur les livres associés.
//Paramètre : bookIds - un tableau contenant les ID des livres.
//Détails de l'implémentation :
//Vide le conteneur d'éléments affichant les livres associés.
//Itère sur le tableau des ID des livres, faisant une requête asynchrone pour chaque ID afin de récupérer les informations du livre.
//Utilise la fonction createBookItem pour créer un élément de livre et l'ajoute au conteneur des livres associés.
//Capture et gère les erreurs potentielles.
async function fetchAndDisplayRelatedBooks(bookIds) {
    const relatedBooksContainer = document.getElementById('related-books-container');
    relatedBooksContainer.innerHTML = '';

    for (const bookId of bookIds) {
        try {
            const bookUrl = `https://gutendex.com/books/${bookId}.json`;
            const response = await fetch(bookUrl);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const book = await response.json();
            const bookElement = createBookItem(book);
            relatedBooksContainer.appendChild(bookElement);
        } catch (error) {
            console.error('Error fetching related book data:', error);
        }
    }
}
//Description de la fonction : Crée et retourne un élément DOM représentant les informations d'un livre.
//Paramètre : book - un objet contenant les informations du livre.
//Détails de l'implémentation :
//Crée un élément div pour l'élément du livre, définit sa classe et son contenu HTML interne (incluant la couverture du livre et le titre).
//Ajoute un écouteur d'événements de clic à l'élément du livre, qui navigue vers une URL contenant l'ID du livre en paramètre.
function createBookItem(book) {
    const bookElement = document.createElement('div');
    bookElement.className = 'book-item';
    bookElement.innerHTML = `
        <img src="${book.formats['image/jpeg'] || 'path/to/default/image.jpg'}" alt="Book Cover">
        <h3>${book.title}</h3>
    `;
    bookElement.onclick = () => {
        window.location.href = `http://localhost:8080/?bookId=${book.id}`;
    };
    return bookElement;
}

//Description de la fonction : Récupère et affiche de manière asynchrone les informations d'un groupe de livres.
//Paramètre : bookIds - un tableau contenant les ID des livres.
//Détails de l'implémentation :
//Assure que le conteneur des résultats de recherche est présent dans le DOM et vide son contenu.
//Itère sur le tableau des ID des livres, récupère de manière asynchrone chaque livre et utilise la fonction displayBook pour l'afficher.
async function fetchAndDisplayBooks(bookIds) {
    const searchResultsContainer = document.getElementById('search-results') || document.createElement('div');
    searchResultsContainer.id = 'search-results';
    searchResultsContainer.innerHTML = '';
    const searchContainer = document.getElementById('search-container');
    if (!searchContainer.contains(searchResultsContainer)) {
        searchContainer.appendChild(searchResultsContainer);
    }

    for (const bookId of bookIds) {
        try {
            const bookUrl = `https://gutendex.com/books/${bookId}.json`;
            const response = await fetch(bookUrl);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const bookData = await response.json();
            displayBook(bookData);
        } catch (error) {
            console.error('Error fetching book data:', error);
        }
    }
}

//Description de la fonction : Affiche les informations d'un livre sur la page.
//Paramètre : book - un objet contenant les informations du livre.
//Détails de l'implémentation :
//Crée un élément DOM représentant les informations du livre, incluant la couverture, le titre et les auteurs.
//Ajoute l'élément du livre au conteneur des résultats de recherche.
function displayBook(book) {
    const searchResultsContainer = document.getElementById('search-results');

    const bookElement = document.createElement('div');
    bookElement.className = 'book-result';
    bookElement.style.display = 'flex';
    bookElement.style.marginBottom = '20px';
    bookElement.style.alignItems = 'center';

    const coverImageUrl = book.formats['image/jpeg'] || 'path/to/default/image.jpg';

    const coverImg = document.createElement('img');
    coverImg.src = coverImageUrl;
    coverImg.alt = 'Book Cover';
    coverImg.style.width = '100px';
    coverImg.style.height = '150px';
    coverImg.style.objectFit = 'cover';
    coverImg.style.marginRight = '20px';

    const infoContainer = document.createElement('div');

    const titleElement = document.createElement('h3');
    titleElement.textContent = book.title;

    const authorsElement = document.createElement('p');
    authorsElement.textContent = 'Authors: ' + book.authors.map(author => author.name).join(', ');

    infoContainer.appendChild(titleElement);
    infoContainer.appendChild(authorsElement);

    bookElement.appendChild(coverImg);
    bookElement.appendChild(infoContainer);

    bookElement.addEventListener('click', () => {
        window.location.search = `?bookId=${book.id}`;
    });

    bookElement.style.cursor = 'pointer';

    searchResultsContainer.appendChild(bookElement);
}


//Description de la fonction : Exécute des opérations d'initialisation une fois que le DOM est complètement chargé.
//Détails de l'implémentation :
//Détermine si afficher les détails d'un livre ou l'interface de recherche en fonction de l'ID du livre dans l'URL.
//Si un ID de livre est présent, récupère et affiche les détails de ce livre et les livres associés.
//Sinon, affiche l'interface de recherche.
document.addEventListener('DOMContentLoaded', async function() {
    const bookId = getBookIdFromUrl();
    const bookTitleHeader = document.getElementById('book-title');
    const bookCoverDiv = document.getElementById('book-cover');
    const bookInfoSection = document.getElementById('book-info');
    const bookDetailsSection = document.getElementById('book-details');
    const searchContainerDiv = document.getElementById('search-container');
    const searchResult = document.getElementById('search-results');
    const book_related = document.getElementById('related-books');

    if (bookId) {
        fetchAndDisplayBookData(bookId);
        bookTitleHeader.style.display = 'block';
        bookCoverDiv.style.display = 'block';
        bookInfoSection.style.display = 'block';
        bookDetailsSection.style.display = 'block';
        searchContainerDiv.style.display = 'none';
        searchResult.style.display = 'none';
        book_related.style.display = 'block';


        const relatedBooksUrl = `http://localhost:8080/projetADaar/getrelatedbookid?id=${encodeURIComponent(bookId)}`;
        try {
            const response = await fetch(relatedBooksUrl);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const relatedBookIds = await response.json(); // 假设返回的是书籍ID的列表
            console.log('Related Book IDs:', relatedBookIds);
            fetchAndDisplayRelatedBooks(relatedBookIds);
        } catch (error) {
            console.error('Error fetching related book IDs:', error);
        }
    } else {
        bookTitleHeader.style.display = 'none';
        bookCoverDiv.style.display = 'none';
        bookInfoSection.style.display = 'none';
        bookDetailsSection.style.display = 'none';
        searchContainerDiv.style.display = 'block';
        searchResult.style.display = 'block';
        book_related.style.display = 'none';

    }
});

//Description de la fonction : Effectue une recherche en fonction de l'entrée de l'utilisateur et du type de recherche sélectionné.
//Détails de l'implémentation :
//Récupère le mot-clé saisi par l'utilisateur et le type de recherche sélectionné.
//Construit l'URL correspondante et fait une requête asynchrone pour récupérer les informations des livres.
//Utilise la fonction fetchAndDisplayBooks pour afficher les résultats de la recherche.
document.getElementById('search-button').addEventListener('click', function() {
    const inputId = document.getElementById('search-box').value;
    const searchType = document.getElementById('search-type').value;

    if (!inputId) {
        alert('Please enter a book ID');
        return;
    }
    const keyword = inputId;
    switch (searchType) {
        case 'regex-title':
            console.log(`Searching for Regex Title with ID: ${inputId}`);
                        if (keyword) {
                                const url = `http://localhost:8080/projetADaar/search/title/RegEx?keyword=${encodeURIComponent(keyword)}`;

                                fetch(url)
                                    .then(response => {
                                        if (!response.ok) {
                                            throw new Error('Network response was not ok');
                                        }
                                        return response.json();
                                    })
                                    .then(data => {
                                        fetchAndDisplayBooks(data);
                                    })
                                    .catch(error => {
                                        console.error('There has been a problem with your fetch operation:', error);
                                    });
                            } else {
                                alert('Please enter a keyword');
                            }
            break;
        case 'regex-keyword':
            console.log(`Searching for Regex Keyword with ID: ${inputId}`);
                    if (keyword) {

                            const url = `http://localhost:8080/projetADaar/search/globalKeyword/RegEx?keyword=${encodeURIComponent(keyword)}`;
                            fetch(url)
                                .then(response => {
                                    if (!response.ok) {
                                        throw new Error('Network response was not ok');
                                    }
                                    return response.json();
                                })
                                .then(data => {
                                    fetchAndDisplayBooks(data);
                                })
                                .catch(error => {
                                    console.error('There has been a problem with your fetch operation:', error);
                                });
                        } else {
                            alert('Please enter a keyword');
                        }
            break;
        case 'normal-title':
            console.log(`Searching for Normal Title with ID: ${inputId}`);
            if (keyword) {
                    const url = `http://localhost:8080/projetADaar/searchtitlekmp?keyword=${encodeURIComponent(keyword)}`;
                    fetch(url)
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('Network response was not ok');
                            }
                            return response.json();
                        })
                        .then(data => {
                            console.log(data);
                            fetchAndDisplayBooks(data);
                        })
                        .catch(error => {
                            console.error('There has been a problem with your fetch operation:', error);
                        });
                } else {
                    alert('Please enter a keyword');
                }

            break;
        case 'normal-keyword':
            console.log(`Searching for Normal Keyword with ID: ${inputId}`);
              if (keyword) {
                const url = `http://localhost:8080/projetADaar/search/globalKeyword/kmp?keyword=${encodeURIComponent(keyword)}`;
                fetch(url)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => {
                        fetchAndDisplayBooks(data);
                    })
                    .catch(error => {
                        console.error('There has been a problem with your fetch operation:', error);
                    });
            } else {
                alert('Please enter a keyword');
            }
            break;
        default:
            console.warn('Unknown search type');
    }
});

//
//Description de la fonction : Récupère l'ID du livre à partir des paramètres de l'URL.
//Détails de l'implémentation : Utilise URLSearchParams pour analyser les paramètres de l'URL de la page actuelle.
function getBookIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('bookId');
}
//Description de la fonction : Récupère et affiche de manière asynchrone les détails d'un livre spécifique.
//Paramètre : bookId - l'ID du livre dont on souhaite afficher les détails.
//Détails de l'implémentation :
//Fait une requête asynchrone à l'API Gutendex pour obtenir les détails du livre.
//Affiche les informations récupérées dans les sections appropriées de la page, y compris le titre, la couverture, les liens de téléchargement, les auteurs, les traducteurs, les sujets, etc.
function fetchAndDisplayBookData(bookId) {
    const apiUrl = `https://gutendex.com/books/${bookId}.json`;

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            document.getElementById('book-title').innerText = data.title;
            document.getElementById('book-cover').innerHTML = `<img src="${data.formats['image/jpeg']}" alt="Book Cover" style="width:100%;">`;

            const downloadLinksTable = document.getElementById('download-links');
            const formats = data.formats;
            let tableContent = `<tr><th>Format</th><th>Link</th></tr>`;

            for (let format in formats) {
                let formatName = format.split(';')[0]; // Clean up format name
                tableContent += `
                    <tr>
                        <td>${formatName}</td>

                        <td><a href="${formats[format]}" target="_blank">Download</a></td>
                    </tr>
                `;
            }

            downloadLinksTable.innerHTML = tableContent;

            const bookInformationTable = document.getElementById('book-information');
            let bookDetailsContent = '';
    
            bookDetailsContent += `<tr><td>Title</td><td>${data.title || ''}</td></tr>`;
            bookDetailsContent += `<tr><td>Author</td><td>${data.authors.map(author => author.name).join(', ') || ''}</td></tr>`;
            bookDetailsContent += `<tr><td>Birth Year</td><td>${data.authors.map(author => author.birth_year || '').join(', ')}</td></tr>`;
            bookDetailsContent += `<tr><td>Death Year</td><td>${data.authors.map(author => author.death_year || '').join(', ')}</td></tr>`;

            bookDetailsContent += `<tr><td>Translators</td><td>${data.translators.length > 0 ? data.translators.map(translator => translator.name).join(', ') : ''}</td></tr>`;
            bookDetailsContent += `<tr><td>Subjects</td><td>${data.subjects.join(', ') || ''}</td></tr>`;
            bookDetailsContent += `<tr><td>Bookshelves</td><td>${data.bookshelves.join(', ') || ''}</td></tr>`;
            bookDetailsContent += `<tr><td>Languages</td><td>${data.languages.join(', ') || ''}</td></tr>`;
            bookDetailsContent += `<tr><td>Copyright</td><td>${data.copyright ? 'Yes' : 'No'}</td></tr>`;
            bookDetailsContent += `<tr><td>Media Type</td><td>${data.media_type || ''}</td></tr>`;
            bookDetailsContent += `<tr><td>Download Count</td><td>${data.download_count || 0}</td></tr>`;
    
            bookInformationTable.innerHTML = bookDetailsContent;
        })
        .catch(error => console.error('Error fetching data:', error));
}



