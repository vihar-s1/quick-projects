from bs4 import BeautifulSoup
import requests, re
from os import path, mkdir


def fetchImages(url: str) -> list[str]:
    r = requests.get(url)
    soup = BeautifulSoup(r.content, "html.parser")
    
    imgs = soup.find_all('img')
    images = []
    
    # extracting the basepath for the images and the webpage
    base_url = re.match(r"^(.*/)(.*\.html)?", url).groups()[0]
        
    for img in imgs:
        # trys data-src to see if lazy load is applied or not
        image = img.get('data-src')
        if image is None:
            image = img.get('src')
            
        # formulating complete url for the image if it is given with reference to webpage folder on server
        if image[:4] != 'http': 
            image = path.join(base_url, image)
        
        # if protocol still not specified even after joining the url, then we explicitly specify the protocol
        if image[:4] != 'http': 
            image = 'https:' + image
        
        images.append(image)
        
    return images


def extractImageName(image_url:str) -> str | None:
    # image = image.replace('%20', '_')  # replace the htmlparsed blank space with underscore
    matchobj = re.findall(r'/([^/]*?)\.(svg|png|jpg|gif|jpeg|webp)', image_url) # extracting image name and extension to save the image
    
    if not matchobj: # no image found
        return None
    # extracting image name and image extension to return the image name
    return f"{matchobj[0][0]}.{matchobj[0][1]}"


def downloadImages(images: list[str]) -> None:
    print(f"attempting to download {len(images)} images...\n")
    
    logFile = open('logFile.txt', 'w')
    
    for image in images:
        print(f"trying: {image}")
        try:
            # getting the image data
            res = requests.get(image, stream=True)
            
            # extracting the image name
            imageName = extractImageName(image)
            
            if not path.exists('images/'):
                mkdir('images')
                
            with open('images/' + imageName, 'wb') as image_file:
                image_file.write(res.content)
                image_file.close()
            
            logFile.write(f"[info] : Downloading Successful for image at url:\n{image}\n\n")
            print("Downloaded Successfully\n")
        except:
            logFile.write(f"[error] : Failed to download the image at url:\n{image}\n\n")
            print("Failed to Download\n")
        
    logFile.close()
            

def __main__():
    url = "http://m.gettywallpapers.com/cool-wallpaper/"
    image_urls = fetchImages(url)
    downloadImages(image_urls)
    

if __name__ == "__main__":
    __main__()